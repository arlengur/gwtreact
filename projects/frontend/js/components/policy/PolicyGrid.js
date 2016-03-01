import React from 'react';
import * as Actions from '../../actions/PolicyActions';

var PolicyGrid = React.createClass({
    render: function() {
        return (
            <div className="row probe-tree-toolbar">
                <div className="probe-probes-find">
                    <div title="Create Policy" className="pull-left margin-0-5 create-btn"
                        onClick={Actions.toggleCreate}/>
                    <div title="Delete" className="pull-left margin-0-5 delete-btn not-allowed" />
                    <input type="text" placeholder="Search" className="pull-right probe-tree-probe-search placeholder-dissappear" />
                </div>
                <div className="head-bg flex-none">
                    <div style={{position: 'relative', width: '100%'}} id="head" className="container-fluid override-padding-0 pull-left">
                        <div style={{position: 'relative', width: '20%'}} className="probe-head-text overflow-ellipsis pull-left">
                            <span className="pull-right header-drag" />
                            <input type="checkbox" className="pull-left chbx-head" />
                            Name
                        </div>
                        <div style={{position: 'relative', width: '15%'}} className="probe-head-text overflow-ellipsis pull-left">
                            <span className="pull-right header-drag" />
                            <span className="pull-left header-drag" />
                            Parameter
                        </div>
                        <div style={{position: 'relative', width: '15%'}} className="probe-head-text overflow-ellipsis pull-left">
                            <span className="pull-right header-drag" />
                            <span className="pull-left header-drag" />
                            Task
                        </div>
                        <div style={{position: 'relative', width: '20%'}} className="probe-head-text overflow-ellipsis pull-left">
                            <span className="pull-right header-drag" />
                            <span className="pull-left header-drag" />
                            Probes
                        </div>
                        <div style={{position: 'relative', width: '30%'}} className="probe-head-text overflow-ellipsis pull-left">
              <span style={{width: 70}} className="probe-head-text overflow-ellipsis pull-right">
                Notifications
              </span>
                            <span className="pull-left header-drag" />
                            Thresholds
                        </div>
                        <div style={{borderColor: '#424242', left: 23}} className="vertical-line" />
                        <div style={{borderColor: '#424242', left: 46}} className="vertical-line" />
                        <div style={{borderColor: '#424242', left: '20%'}} className="vertical-line" />
                        <div style={{borderColor: '#424242', left: '35%'}} className="vertical-line" />
                        <div style={{borderColor: '#424242', left: '50%'}} className="vertical-line" />
                        <div style={{borderColor: '#424242', left: '70%'}} className="vertical-line" />
                        <div style={{borderColor: '#424242', right: 70}} className="vertical-line" />
                    </div>
                </div>
                <div className="probe-tree probe-tree-background">
                    <ul style={{position: 'relative'}} className="probe-tree-background override-margin-0 override-padding-0">
                        <li className="probe-tree-li">
                            <div id="tree" className="container-fluid override-padding-0">
                                <div style={{width: '20%', textDecoration: 'underline'}} className="pull-left overflow-ellipsis">
                                    <input type="checkbox" className="pull-left chbx" />
                                    <div style={{marginLeft: 2}} className="pull-left tsk-audio" />
                                    <span className="small-text probe-tree-line-text">Too quite integrated loudness</span>
                                </div>
                                <div style={{position: 'relative', width: '15%'}} className="probe-tree-line-text pull-left overflow-ellipsis">Integrated loudness</div>
                                <div style={{position: 'relative', width: '15%'}} className="probe-tree-line-text pull-left overflow-ellipsis">HomeNN - RTPStatistics</div>
                                <div style={{position: 'relative', width: '20%'}} className="probe-tree-line-text pull-left overflow-ellipsis">
                                    <span style={{color: '#6A97D5'}}>(611) </span>FE12-2345-DA1E-FA9E
                                </div>
                                <div style={{position: 'relative', width: '30%'}} className="probe-tree-line-text pull-left overflow-ellipsis">
                                    <div className="pull-left threshold-wrapper">
                                        <div style={{left: 'calc(14% - 3px)'}} className="threshold-text">4</div>
                                        <div style={{left: 'calc(28% - 3px)'}} className="threshold-text">8</div>
                                        <div style={{left: 'calc(42% - 3px)'}} className="threshold-text">12</div>
                                        <div style={{left: 'calc(56% - 3px)'}} className="threshold-text">16</div>
                                        <div style={{left: 'calc(71% - 3px)'}} className="threshold-text">20</div>
                                        <div style={{left: 'calc(86% - 3px)'}} className="threshold-text">24</div>
                                        <svg preserveAspectRatio="none" viewBox="0 0 300 23" height="100%" width="100%">
                                            <defs>
                                                <radialGradient r="100%" cy="100%" cx="100%" id="grad1">
                                                    <stop offset="0%" stopOpacity={1} stopColor="#00c200" />
                                                    <stop offset="100%" stopOpacity={0} stopColor="#00c200" />
                                                </radialGradient>
                                                <linearGradient y2="0%" x2="0%" y1="100%" x1="0%" id="grad2">
                                                    <stop offset="0%" stopOpacity={1} stopColor="#4d4d4d" />
                                                    <stop offset="100%" stopOpacity={0} stopColor="#4d4d4d" />
                                                </linearGradient>
                                                <linearGradient y2="0%" x2="0%" y1="100%" x1="0%" id="grad3">
                                                    <stop offset="0%" stopOpacity={1} stopColor="#b5b5b5" />
                                                    <stop offset="100%" stopOpacity={0} stopColor="#b5b5b5" />
                                                </linearGradient>
                                                <linearGradient y2="0%" x2="0%" y1="100%" x1="0%" id="grad4">
                                                    <stop offset="0%" stopOpacity={1} stopColor="#998237" />
                                                    <stop offset="100%" stopOpacity={0} stopColor="#998237" />
                                                </linearGradient>
                                                <linearGradient y2="0%" x2="0%" y1="100%" x1="0%" id="grad5">
                                                    <stop offset="0%" stopOpacity={1} stopColor="#d4a300" />
                                                    <stop offset="100%" stopOpacity={0} stopColor="#d4a300" />
                                                </linearGradient>
                                                <linearGradient y2="0%" x2="0%" y1="100%" x1="0%" id="grad6">
                                                    <stop offset="0%" stopOpacity={1} stopColor="#ff6600" />
                                                    <stop offset="100%" stopOpacity={0} stopColor="#ff6600" />
                                                </linearGradient>
                                                <radialGradient r="100%" cy="100%" cx="0%" id="grad7">
                                                    <stop offset="0%" stopOpacity={1} stopColor="#ff0000" />
                                                    <stop offset="100%" stopOpacity={0} stopColor="#ff0000" />
                                                </radialGradient>
                                            </defs>
                                            <polygon points="0,23 43,23 43,0 0,0" strokeWidth={0} fill="url(#grad1)" />
                                            <polygon points="43,23 86,23 86,0 43,0" strokeWidth={0} fill="url(#grad2)" />
                                            <polygon points="86,23 129,23 129,0 86,0" strokeWidth={0} fill="url(#grad3)" />
                                            <polygon points="129,23 172,23 172,0 129,0" strokeWidth={0} fill="url(#grad4)" />
                                            <polygon points="172,23 215,23 215,0 172,0" strokeWidth={0} fill="url(#grad5)" />
                                            <polygon points="215,23 258,23 258,0 215,0" strokeWidth={0} fill="url(#grad6)" />
                                            <polygon points="258,23 300,23 300,0 258,0" strokeWidth={0} fill="url(#grad7)" />
                                        </svg>
                                    </div>
                                    <div className="send-email pull-right" />
                                    <div className="send-sms pull-right" />
                                    <div className="send-alert pull-right" />
                                </div>
                            </div>
                        </li>
                        <div style={{borderColor: '#424242', left: 23}} className="vertical-line" />
                        <div style={{borderColor: '#424242', left: 46}} className="vertical-line" />
                        <div style={{borderColor: '#424242', left: '20%'}} className="vertical-line" />
                        <div style={{borderColor: '#424242', left: '35%'}} className="vertical-line" />
                        <div style={{borderColor: '#424242', left: '50%'}} className="vertical-line" />
                        <div style={{borderColor: '#424242', left: '70%'}} className="vertical-line" />
                        <div style={{borderColor: '#424242', right: 70}} className="vertical-line" />
                    </ul>
                    <div style={{borderColor: '#424242', left: 23}} className="vertical-line" />
                    <div style={{borderColor: '#424242', left: 46}} className="vertical-line" />
                    <div style={{borderColor: '#424242', left: '20%'}} className="vertical-line" />
                    <div style={{borderColor: '#424242', left: '35%'}} className="vertical-line" />
                    <div style={{borderColor: '#424242', left: '50%'}} className="vertical-line" />
                    <div style={{borderColor: '#424242', left: '70%'}} className="vertical-line" />
                    <div style={{borderColor: '#424242', right: 70}} className="vertical-line" />
                </div>
            </div>
        );
    }
});

export default PolicyGrid;