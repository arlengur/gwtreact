import React from 'react';
import DetailsActions from '../../actions/DetailsActions';
import AppUserStore from '../../stores/AppUserSettingsStore';
import OverviewLine from './OverviewLine';

var drag = Object.freeze({
    NONE: 0,
    LEFT: 1,
    RIGHT: 2,
    MIDDLE: 3
});

var MIN_INTERVAL = 1000 * 60;

var TimelineScrollbar = React.createClass({
    getInitialState: function() {
        return {
            dragging: drag.NONE,
            dragMidOffset: 0,
            startPos: 0,
            endPos: 1
        }
    },
    posToTimestamp: function(pos) {
        var length = this.props.interval.end - this.props.interval.start;
        return this.props.interval.start + length * pos;
    },
    onMouseDown: function(e, dragTarget) {
        var dragMidOffset = 0;
        if(dragTarget == drag.MIDDLE) {
            var wrapper = React.findDOMNode(this.refs.wrapper);
            var dragCenter = React.findDOMNode(this.refs['drag-center']);
            dragMidOffset = (e.pageX - dragCenter.getBoundingClientRect().left)/wrapper.getBoundingClientRect().width;
        }
        this.setState({
            dragging: dragTarget,
            dragMidOffset: dragMidOffset
        });
        e.stopPropagation();
        e.preventDefault();
    },
    onMouseUp: function(e) {
        this.setState({dragging: drag.NONE});
        DetailsActions.selectInterval({
            start: this.posToTimestamp(this.state.startPos),
            end: this.posToTimestamp(this.state.endPos)
        });
        e.stopPropagation();
        e.preventDefault();
    },
    onMouseMove: function(e) {
        var minInterval = MIN_INTERVAL / (this.props.interval.end - this.props.interval.start);
        var wrapper = React.findDOMNode(this.refs.wrapper);
        var offset = (e.pageX - wrapper.offsetLeft)/wrapper.getBoundingClientRect().width;
        if(offset < 0) {
            offset = 0;
        } else if (offset > 1) {
            offset = 1
        }
        switch(this.state.dragging) {
            case drag.LEFT:
                if(offset + minInterval > this.state.endPos) {
                    offset = this.state.endPos - minInterval;
                }
                this.setState({startPos: offset});
                break;
            case drag.RIGHT:
                if(offset - minInterval < this.state.startPos) {
                    offset = this.state.startPos + minInterval;
                }
                this.setState({endPos: offset});
                break;
            case drag.MIDDLE:
                var intLength = this.state.endPos - this.state.startPos;
                var intStart = offset - this.state.dragMidOffset;
                var newStart, newEnd;
                if(intStart < 0) {
                    newStart = 0;
                    newEnd = intLength;
                } else if (intStart + intLength > 1){
                    newEnd = 1;
                    newStart = 1 - intLength;
                } else {
                    newStart = intStart;
                    newEnd = intStart + intLength;
                }
                this.setState({startPos: newStart, endPos: newEnd});
                break;
            case drag.NONE:
            default:
                // do nothing
        }
        e.stopPropagation();
        e.preventDefault();
    },
    componentDidUpdate: function (props, state) {
        if (this.state.dragging && !state.dragging) {
            document.addEventListener('mousemove', this.onMouseMove);
            document.addEventListener('mouseup', this.onMouseUp);
        } else if (!this.state.dragging && state.dragging) {
            document.removeEventListener('mousemove', this.onMouseMove);
            document.removeEventListener('mouseup', this.onMouseUp);
        }
    },
    render: function() {
        var startTimestamp = this.posToTimestamp(this.state.startPos);
        var endTimestamp = this.posToTimestamp(this.state.endPos);
        var duration = endTimestamp - startTimestamp;
        var startPerc = this.state.startPos * 100;
        var endPerc = (1 - this.state.endPos) * 100;
        var widthPerc = (this.state.endPos - this.state.startPos) * 100;
        var cursorStyle = widthPerc < 33 ? "TlScr-cursor-close" : widthPerc < 66 ? "TlScr-cursor-neutral": "TlScr-cursor-edge";
        var cursorLeftStyle = startPerc < 17 ? "TlScr-cursor-edge" : cursorStyle ;
        var cursorRightStyle = endPerc < 17 ? "TlScr-cursor-edge" : cursorStyle ;
        return <div className="TlScr-wrapper" ref="wrapper">
            <div className="TlScr-background">
                <OverviewLine interval={this.props.interval}
                              alertGroups={this.props.alertGroups}
                              separators={this.props.separators}/>
                <div className="TlScr-start-date tiny-text">{AppUserStore.localizeDateTime(this.props.interval.start)}</div>
                <div className="TlScr-end-date tiny-text">{AppUserStore.localizeDateTime(this.props.interval.end)}</div>
            </div>
            <div className="TlScr-left" style={{width: ''+startPerc+'%'}}></div>
            <div className="TlScr-right" style={{width: ''+endPerc+'%'}}></div>
            <div className="TlScr-focused" style={{left: ''+startPerc+'%', width: ''+widthPerc+'%'}}>
                <div ref="drag-center"
                     className={"TlScr-drag-center "+(this.state.dragging==drag.MIDDLE?"grabbing":"grab")}
                     onMouseDown={function(e) {this.onMouseDown(e, drag.MIDDLE)}.bind(this)}>
                </div>
                <div className="TlScr-drag-left"
                     onMouseDown={function(e) {this.onMouseDown(e, drag.LEFT)}.bind(this)}>
                    <div className="arrow-icon TlScr-drag-icon-left"></div>
                    {this.state.startPos != 0 ?
                        <div className={"TlScr-interval-start tiny-text " + cursorLeftStyle}>
                            {AppUserStore.localizeDateTime(startTimestamp)}
                        </div>
                        : ""}
                </div>
                <div className="TlScr-drag-right"
                     onMouseDown={function(e) {this.onMouseDown(e, drag.RIGHT)}.bind(this)}>
                    <div className="arrow-icon TlScr-drag-icon-right"></div>
                    {this.state.endPos != 1 ?
                        <div className={"TlScr-interval-end tiny-text " + cursorRightStyle}>
                            {AppUserStore.localizeDateTime(endTimestamp)}
                        </div>
                        : ""}
                </div>
                <div className="TlScr-int-length tiny-text">
                    {AppUserStore.localizeTimeDuration(duration)}
                </div>
            </div>
        </div>
    }
});

export default TimelineScrollbar;
