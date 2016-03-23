import React from 'react';
import _ from 'lodash';

var Select = React.createClass({
    getInitialState: function() {
        return {
            value: this.props.value
        }
    },
    // Enable changing value via props
    componentWillReceiveProps: function(props) {
        this.setState({value: props.value});
    },
    _onChange: function(event){
        this.setState({value: event.target.value});
        if(typeof this.props.onChange == 'function') this.props.onChange(event.target.value);
    },
    render: function () {
        const style = typeof this.props.style == 'undefined' ? {} : this.props.style;
        return <div className={this.props.className}
                    style={_.assign(style, {
                            width: this.props.width,
                            overflow: 'hidden',
                            background: 'url("img/dropdown/dropdownButton_0.png") no-repeat right #222222'
                        })} >
            <select id={this.props.id} className="small-text text23 appearance-none"
                    style={{width: this.props.width, border: 0, background: 'transparent', paddingRight: '20px', paddingLeft: '5px'}}
                    value={this.state.value}
                    disabled={this.props.disabled}
                    onChange={this._onChange}>
                {_.map(this.props.options, (opt) => {
                    var val, text;
                    if(_.isArray(opt)) {
                        val = opt[0];
                        text = opt[1];
                    } else {
                        val = opt;
                        text = opt;
                    }
                    return <option value={val} key={opt} style={{background: '#222222', borderLeft: 'none'}}>{text}</option>}
                )}
            </select>
        </div>
    }
});

export default Select;