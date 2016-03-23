import React from 'react';
import * as Actions from '../../actions/RecordScheduleActions';
import ConfirmDialog from '../../components/common/ConfirmDialog';
import AppActions from '../../actions/AppActions';
import AppConstants from '../../constants/AppConstants';
import {default as Store} from '../../stores/RecordScheduleStore';
import Radio from '../common/Radio';
import Input from '../common/TextInputTypeahead';
import Select from '../common/Select';
import moment from 'moment';
import 'moment-timezone';
import "moment/locale/ru";
import 'moment-duration-format';
import 'moment-range';
import _ from 'lodash';
import AppUserSettingsStore from '../../stores/AppUserSettingsStore';
import i18n from '../../constants/i18nConstants';
import Immutable from 'immutable';
import Misc from '../../util/Misc';
import DatePicker from 'react-datepicker';
import DateTimePicker from 'react-widgets/lib/DateTimePicker';
import globalizeLocalizer  from 'react-widgets/node_modules/globalize/lib/globalize';
import cultures  from 'react-widgets/node_modules/globalize/lib/cultures/globalize.cultures';
const ls = AppUserSettingsStore.localizeStringCFL;
const utsFormat = 'YYYYMMDDTHHmmss';

var SaveScheduleConfirmText = React.createClass({
    render: function () {
        var details = [ls(i18n.SCHEDULE_SAVE_WARN)];
        details.push(<br key={"b2"}/>);
        details.push(ls(i18n.TASKS) + ' (' + this.props.tasks.length + '):');
        return <div className="flex flex-11a flex-col confirm-panel">
            <div className="flex-none confirm-text">
                {details}
            </div>
            <div className="flex flex-11a flex-col confirm-probes-text">
                {_.chain(this.props.tasks)
                    .sortBy((p)=>p.name.toUpperCase())
                    .map((task, id) => [<span className="flex-none" key={id}>{task.name}</span>])
                    .flatten()
                    .value()}
            </div>
        </div>
    }
});


const CrudRecordSchedule = React.createClass({
    getInitialState: function () {
        var initDateTime = moment();
        var shiftDateTime = moment().add(1, 'hours');
        return {
            startDate: initDateTime.format('DD/MM/YYYY'),
            startTime: initDateTime.format('HH:mm:ss'),
            finishDate: shiftDateTime.format('DD/MM/YYYY'),
            finishTime: shiftDateTime.format('HH:mm:ss'),
            duration: moment.duration(moment(shiftDateTime).diff(initDateTime)).format("HH:mm:ss"),
            interval: true,
            comment: '',
            timezone: this.props.store.get('timeZoneId'),
            editActive: this.props.store.get('editActive')
        }
    },
    componentDidUpdate: function () {
        this.resizeHead();
    },
    componentDidMount: function () {
        this.resizeHead();
        window.addEventListener('resize', this.resizeHeadDelayed);
    },
    componentWillUnmount: function () {
        window.removeEventListener('resize', this.resizeHeadDelayed);
    },
    resizeHeadDelayed: function () {
        setTimeout(this.resizeHead, 50);
    },
    resizeHead: function () {
        var tree = React.findDOMNode(this.refs.treeWidth);
        var treeElement = tree.getElementsByClassName('tree-col-width')[0];
        var head = React.findDOMNode(this.refs.headWidth);
        var headElement = head.getElementsByClassName('tree-head-width')[0];
        if (treeElement != null && headElement != null) {
            headElement.style.width = treeElement.getBoundingClientRect().width + 'px';
        }
    },
    updateStartDate: function (sDate, sTime, fDate, fTime) {
        if (sDate != null) {
            this.setState({
                startDate: sDate,
                duration: this.getDuration(sDate, sTime, fDate, fTime)
            });
        }
    },
    updateStartTime: function (sDate, sTime, fDate, fTime) {
        if (sTime != null) {
            this.setState({
                startTime: sTime,
                duration: this.getDuration(sDate, sTime, fDate, fTime)
            });
        }
    },
    updateFinishDate: function (sDate, sTime, fDate, fTime) {
        if (fDate != null) {
            this.setState({
                finishDate: fDate,
                duration: this.getDuration(sDate, sTime, fDate, fTime)
            });
        }
    },
    updateFinishTime: function (sDate, sTime, fDate, fTime) {
        if (fTime != null) {
            this.setState({
                finishTime: fTime,
                duration: this.getDuration(sDate, sTime, fDate, fTime)
            });
        }
    },
    getDuration: function (sDate, sTime, fDate, fTime) {
        let calcDuration = moment.duration(moment(fDate + fTime, 'DD/MM/YYYYHH:mm:ss').diff(moment(sDate + sTime, 'DD/MM/YYYYHH:mm:ss'))),
            formatedDuration = calcDuration.format("HH:mm:ss"),
            isExceed = calcDuration > 604800000;
        this.setState({isExceed: isExceed});
        return formatedDuration;
    },
    updateDuration: function (value) {
        this.setState({duration: value});
    },
    updateRadio: function (val) {
        this.setState({interval: val});
    },
    addEventLine: function (startDate, startTime, finishDate, finishTime, comment, localTimeZone) {
        var date = moment(startDate, ['DD/MM/YYYY'], true).isValid() && moment(finishDate, ['DD/MM/YYYY'], true).isValid();
        var time =  moment(startTime, ['HH:mm:ss'], true).isValid() &&  moment(finishTime, ['HH:mm:ss'], true).isValid();
        if (date && time) {
            var line = {};
            line.begin = this.getDateTimeString(startDate + startTime, this.state.timezone, localTimeZone);
            line.end = this.getDateTimeString(finishDate + finishTime, this.state.timezone, localTimeZone);
            line.comment = comment;

            if (this.isDateOverlapped(line)) {
                Actions.showError(i18n.DATE_RANGES_CANNOT_OVERLAPPED);
            } else if (moment(line.begin).isAfter(moment(line.end))) {
                Actions.showError(i18n.WRONG_DATE_RANGE);
            } else {
                Actions.addEventLine(line);
                this.setState({
                    startDate: this.addHourToTime(startDate + startTime, 'DD/MM/YYYY'),
                    startTime: this.addHourToTime(startDate + startTime, 'HH:mm:ss'),
                    finishDate: this.addHourToTime(finishDate + finishTime, 'DD/MM/YYYY'),
                    finishTime: this.addHourToTime(finishDate + finishTime, 'HH:mm:ss'),
                    comment: ''
                });
            }
        } else if (!date) {
            Actions.showError(i18n.WRONG_DATE_FORMAT);
        } else if (!time) {
            Actions.showError(i18n.WRONG_TIME_FORMAT);
        }
    },
    addHourToTime: (dateTime, format) => {
        return moment(dateTime, 'DD/MM/YYYYHH:mm:ss').add(1, 'hours').format(format)
    },
    isDateOverlapped: function (date) {
        return !_.every(this.props.store.get('eventList'), (event) => !(moment.range(date.begin, date.end).overlaps(moment.range(event.begin, event.end))));
    },
    getDateTimeString: (value, timezone, localTimeZone) => {
        var tz = timezone == 'Probe' ? localTimeZone : timezone;
        return moment.tz(value, 'DD/MM/YYYYHH:mm:ss', tz).utc().format(utsFormat);
    },
    getDateTimeUTCString: function (value, timezone, localTimeZone) {
        // get time in local timezone
        var sourceString = moment.utc(value, utsFormat).tz(localTimeZone).format(utsFormat);
        // save time in probe timezone and convert to utc
        return moment.tz(sourceString, utsFormat, timezone).utc().format(utsFormat);
    },
    getTimeZone: (selectedTz, taskTz) => {
        return selectedTz == 'Probe' ? taskTz : selectedTz;
    },
    sendNewSchedule: function () {
        var localTimeZone = this.props.store.get('localTimeZone');
        if (this.state.editActive) {
            var task = _.find(this.props.store.get('tasks'), (task)=> {return this.props.store.get('selectedTasks').contains(task.entityKey)});
            Actions.updateRecordScheduler(this.props.store.get('selectedProbes'), JSON.stringify({
                agentKey: task.agentKey,
                taskKey: task.entityKey,
                timeZone: this.getTimeZone(this.state.timezone, task.timeZone),
                eventList: _.map(this.props.store.get('eventList'), event=> {
                    return {
                        begin: this.state.timezone == 'Probe' ? this.getDateTimeUTCString(event.begin, task.timeZone, localTimeZone) : event.begin,
                        end: this.state.timezone == 'Probe' ? this.getDateTimeUTCString(event.end, task.timeZone, localTimeZone) : event.end,
                        comment: event.comment
                    }
                })
            }));
        } else {
            _.chain(this.props.store.get('tasks'))
                .filter((task)=> {return this.props.store.get('selectedTasks').contains(task.entityKey)})
                .groupBy((t)=>t.timeZone)
                .each((tasks, timezone)=> {
                    Actions.createRecordScheduler(this.props.store.get('selectedProbes'), JSON.stringify({
                        timeZone: this.getTimeZone(this.state.timezone, timezone),
                        eventList: _.map(this.props.store.get('eventList'), event=> {
                            return {
                                begin: this.state.timezone == 'Probe' ? this.getDateTimeUTCString(event.begin, timezone, localTimeZone) : event.begin,
                                end: this.state.timezone == 'Probe' ? this.getDateTimeUTCString(event.end, timezone, localTimeZone) : event.end,
                                comment: event.comment
                            }
                        }),
                        taskAgentMap: Immutable.Map(_.map(tasks, task=> [task.entityKey, task.agentKey])).toJS()
                    }));
                })
                .value();
        }
        Actions.closeSchedule();
    },
    clear: function () {
        var initDateTime = moment();
        var shiftDateTime = moment().add(1, 'hours');
        this.setState({
            startDate: initDateTime.format('DD/MM/YYYY'),
            startTime: initDateTime.format('HH:mm:ss'),
            finishDate: shiftDateTime.format('DD/MM/YYYY'),
            finishTime: shiftDateTime.format('HH:mm:ss'),
            duration: moment.duration(moment(shiftDateTime).diff(initDateTime)).format("HH:mm:ss"),
            comment: ''
        });
    },
    render: function () {
        var selectedTasks = _.chain(this.props.store.get('tasks'))
            .filter((task)=> {return this.props.store.get('selectedTasks').contains(task.entityKey)}).value();
        var locale = AppUserSettingsStore.getLocale();
        var localTimeZone = this.props.store.get('localTimeZone');
        let isExceed = this.state.isExceed;
        return <div className="modal flex">
            <div className="grey-page"></div>
            <div className="modal-dialog bigmodal flex flex-col" style={{width: '53%', height: '530px', background: '#000'}}>
                <div className="modal-head flex-none">
                    <img src="img/modal_close.png" className="pull-right clickable" style={{paddingTop: '6px', paddingRight: '6px'}}
                         onClick={() => Actions.closeSchedule()}/>
                    <h4 className="modal-title">{ls(this.state.editActive ? i18n.EDIT_SCHEDULE : i18n.CREATE_SCHEDULE)}</h4>
                </div>
                <div className="flex flex-11a" style={{overflow: 'hidden', margin: '10px 10px 0'}}>
                    <div className="override-padding-0 flex flex-col black-right" style={{width: locale == 'en' ? '208px' : '243px', background: '#424242'}}>
                        <div className="row flex-none" style={{margin: '0', padding: '5px'}}>
                            <Select width="108px" className="flex-none pull-right"
                                    options={_.map(this.props.store.get('timezones'), (timezone) => {return [timezone.id, timezone.description]})}
                                    value={this.state.timezone}
                                    onChange={(v)=>this.setState({timezone: v})}
                            />
                            <span className="flex-none small-text pull-left text23 margin-left-5">
                                {ls(i18n.TIMEZONE) + ':'}
                            </span>
                        </div>
                        <div className="row flex-none" style={{margin: '0', padding: '5px'}}>
                            <DateTimePicker
                                className={'pull-right'}
                                time={false}
                                defaultValue={moment(this.state.startDate, 'DD/MM/YYYY').toDate()}
                                culture={locale}
                                onChange={(date)=>this.updateStartDate(moment(date).format('DD/MM/YYYY'), this.state.startTime, this.state.finishDate, this.state.finishTime)}/>
                            <span className="flex-none small-text text23 pull-left" style={{marginLeft: '23px'}}>
                                {ls(i18n.START) + ':'}
                            </span>
                        </div>
                        <div className="row flex-none" style={{margin: '0', padding: '5px'}}>
                            <DateTimePicker
                                className={'pull-right'}
                                calendar={false}
                                value={moment(this.state.startTime, 'HH:mm:ss').toDate()}
                                culture={locale}
                                format={'HH:mm:ss'}
                                onSelect={(v) => this.updateStartTime(this.state.startDate, moment(v).format("HH:mm:ss"), this.state.finishDate, this.state.finishTime)}/>
                        </div>
                        <div className="row flex-none" style={{margin: '0', padding: '5px'}}>
                            <DateTimePicker
                                className={'pull-right'}
                                time={false}
                                defaultValue={moment(this.state.finishDate, 'DD/MM/YYYY').toDate()}
                                culture={locale}
                                onChange={(date)=>this.updateFinishDate(this.state.startDate, this.state.startTime, moment(date).format('DD/MM/YYYY'), this.state.finishTime)}/>
                            <Radio id="finish" className="pull-left" style={{lineHeight: '23px', margin: '0 4px 0 5px'}}
                                   checked={this.state.interval}
                                   onChange={() => this.updateRadio(true)}/>
                            <span className="flex-none small-text text23 pull-left">
                                {ls(i18n.FINISH) + ':'}
                            </span>
                        </div>
                        <div className="row flex-none" style={{margin: '0', padding: '5px'}}>
                            <DateTimePicker
                                className={'pull-right'}
                                calendar={false}
                                value={moment(this.state.finishTime, 'HH:mm:ss').toDate()}
                                culture={locale}
                                format={'HH:mm:ss'}
                                onSelect={(v) => this.updateFinishTime(this.state.startDate, this.state.startTime, this.state.finishDate, moment(v).format("HH:mm:ss"))}/>
                        </div>
                        <div className="row flex-none" style={{margin: '0', padding: '5px'}}>
                            <Input className="flex-11a search-field record-disabled pull-right"
                                   style={{height: '23px', width: '108px', textAlign: 'center',border:isExceed ? '1px solid red' : '0px solid red'}}
                                   disabled="true"
                                   value={this.state.duration}
                                   onBlur={(v) => this.updateDuration(v)}/>
                            <Radio id="duration" className="pull-left" style={{lineHeight: '23px', margin: '0 4px 0 5px'}}
                                   disabled="true"
                                   checked={!this.state.interval}
                                   onChange={() => this.updateRadio(true)}/>
                            <span className="flex-none small-text text23 pull-left">
                                {ls(i18n.DURATION) + ':'}
                            </span>
                        </div>
                        <div className="row flex-none" style={{margin: '0', padding: '5px',display:isExceed ? '' : 'none'}}>
                            <span className="flex-none tiny-text pull-left" style={{marginLeft: '5',color:'red', wordBreak: 'break-all'}}>
                                {ls(i18n.DURATION_ERR)}
                            </span>
                        </div>

                        <div className="row flex-none"></div>
                        <div className="row flex-none" style={{margin: '0', padding: '5px'}}>
                            <span className="flex-none small-text text23 margin-left-5">
                                {ls(i18n.COMMENT) + ':'}
                            </span>
                            <textarea className="comm-area" rows="6" style={{backgroundColor: '#222', color: '#fff'}}
                                      value={this.state.comment}
                                      onChange={()=>this.setState({comment: event.target.value})}
                            />
                        </div>
                        <div className="row flex-none" style={{margin: '0', padding: '0 5px'}}>
                            <button type="button" className="btn btn-primary config-btn pull-left" disabled={isExceed}
                                    onClick={() => this.addEventLine(this.state.startDate, this.state.startTime, this.state.finishDate, this.state.finishTime, this.state.comment, localTimeZone)}>
                                {ls(i18n.ADD)}
                            </button>
                            <button type="button" className="btn btn-primary config-btn margin-left-5 pull-right"
                                    onClick={()=>this.clear()}>
                                {ls(i18n.CLEAR)}
                            </button>
                        </div>
                    </div>
                    <div className="row override-padding-0 override-margin-0 flex flex-col"
                         style={{width: locale == 'en' ? 'calc(100% - 208px)' : 'calc(100% - 243px)', backgroundColor: '#000'}}>
                        <div id="tab" className="flex-none" style={{backgroundColor: '#000'}}>
                            <input type="radio" name="table" id="n1" checked style={{display: 'none'}}/>
                            <label htmlFor="n1" className="tab-label">
                                {ls(i18n.TABLE)}
                            </label>
                        </div>
                        <div id="table" className="flex-11a flex flex-col" style={{overflowY: 'auto',  background: '#424242'}}>
                            <TaskTreeHead ref="headWidth"/>
                            <TaskTree eventList={this.props.store.get('eventList')} timezone={this.state.timezone} ref="treeWidth" localTimezone={localTimeZone}/>
                        </div>
                    </div>
                </div>
                <div className="flex-none" style={{backgroundColor: '#000', margin: '4px 10px 10px'}}>
                    <button type="button" className="btn btn-primary config-btn pull-left"
                            onClick={() => AppActions.showConfirmModal(
                                ls(i18n.SCHEDULE_SAVE),
                                <SaveScheduleConfirmText
                                    tasks={selectedTasks}/>,
                                AppConstants.RECORD_SCHEDULE.TOGGLE_CLOSE,
                                () => this.sendNewSchedule())}>
                        {ls(i18n.OK)}
                    </button>
                    <button type="button" className="btn btn-primary config-btn margin-left-5 pull-right"
                            onClick={() => Actions.closeSchedule()}>
                        {ls(i18n.CANCEL)}
                    </button>
                </div>

            </div>
            <ConfirmDialog/>
        </div>
    }
});

const TaskTreeHead = React.createClass({
    render: function () {
        return <div className="head-bg flex-none" style={{margin: '4px 4px 0'}}>
            <div className="container-fluid override-padding-0 pull-left tree-head-width" style={{position: 'relative'}}>
                <div className="probe-head-text overflow-ellipsis pull-left" style={{width: '25%'}}>
                    {ls(i18n.START)}
                </div>
                <div className="probe-head-text overflow-ellipsis pull-left" style={{width: '25%'}}>
                    {ls(i18n.FINISH)}
                </div>
                <div className="probe-head-text overflow-ellipsis pull-left" style={{width: '15%'}}>
                    {ls(i18n.DURATION)}
                </div>
                <div className="probe-head-text overflow-ellipsis pull-left" style={{width: '35%'}}>
                    <span className="pull-left overflow-ellipsis" style={{width: 'calc(100% - 27px)'}}>
                        {ls(i18n.COMMENT)}
                    </span>
                </div>
                <div className="vertical-line" style={{borderColor: '#424242', left: '25%'}}></div>
                <div className="vertical-line" style={{borderColor: '#424242', left: '50%'}}></div>
                <div className="vertical-line" style={{borderColor: '#424242', left: '65%'}}></div>
                <div className="vertical-line" style={{borderColor: '#424242', right: '27px'}}></div>
            </div>
        </div>
    }
});

const TaskTree = React.createClass({
    render: function () {
        return <div className="probe-tree-background flex-11a"
                    style={{margin: '0 4px 4px 4px', minHeight: '0', overflowY: 'auto', border: '0', backgroundColor: '#3D3B3B',  color: '#FFF',  fontSize: '12px', position: 'relative'}}>
            <ul className="probe-tree-background override-margin-0 override-padding-0 tree-col-width" style={{position: 'relative'}}>
                {_.map(_.sortBy(this.props.eventList, (event)=>event.begin), (event, id) => {
                    return <TaskNode key={id} event={event} timezone={this.props.timezone} localTimezone={this.props.localTimezone}/>
                })}
                <div className="vertical-line" style={{borderColor: '#424242', left: '25%'}}></div>
                <div className="vertical-line" style={{borderColor: '#424242', left: '50%'}}></div>
                <div className="vertical-line" style={{borderColor: '#424242', left: '65%'}}></div>
                <div className="vertical-line" style={{borderColor: '#424242', right: '27px'}}></div>
            </ul>
            <div className="vertical-line" style={{borderColor: '#424242', left: '25%'}}></div>
            <div className="vertical-line" style={{borderColor: '#424242', left: '50%'}}></div>
            <div className="vertical-line" style={{borderColor: '#424242', left: '65%'}}></div>
            <div className="vertical-line" style={{borderColor: '#424242', right: '27px'}}></div>
        </div>
    }
});

const TaskNode = React.createClass({
    getDisplayTime: function (value) {
        var tz = this.props.timezone == 'Probe' ? this.props.localTimezone : this.props.timezone;
        return moment.utc(value, utsFormat).tz(tz).format('MMM D, YYYY, HH:mm:ss');
    },
    render: function () {
        var event = this.props.event;
        return <li className="probe-tree-li">
            <div className="container-fluid override-padding-0">
                <div className="probe-tree-line-text pull-left overflow-ellipsis" style={{width: '25%', textAlign: 'center'}}>
                    {this.getDisplayTime(event.begin)}
                </div>
                <div className="probe-tree-line-text pull-left overflow-ellipsis" style={{width: '25%', textAlign: 'center'}}>
                    {this.getDisplayTime(event.end)}
                </div>
                <div className="probe-tree-line-text pull-left overflow-ellipsis" style={{width: '15%', textAlign: 'center'}}>
                    {moment.duration(moment(moment(event.end)).diff(moment(event.begin))).format("HH:mm:ss")}
                </div>
                <div className="probe-tree-line-text pull-left overflow-ellipsis" style={{width: '35%'}}>
                    <span className="pull-left overflow-ellipsis" style={{width: 'calc(100% - 23px)'}}>{event.comment}</span>
                    <div className="cross-icon margin-left-5 clickable pull-right"
                         title={ls(i18n.REMOVE_BUTTON)}
                         onClick={() => Actions.removeEventLine(event.begin)}
                    ></div>
                </div>
            </div>
        </li>
    }
});

export default CrudRecordSchedule;
