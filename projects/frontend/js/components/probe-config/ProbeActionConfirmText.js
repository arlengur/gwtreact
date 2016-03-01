import React from 'react';
import _ from 'lodash';
import AppUserStore from '../../stores/AppUserSettingsStore';
import i18n from '../../constants/i18nConstants';

var ProbeActionConfirmText = React.createClass({
    render: function() {
        var details = [AppUserStore.localizeString(i18n.PERFORMING) + ' ' + AppUserStore.localizeString(this.props.action)];
        if(this.props.fileName != undefined) {
            details.push(<br key={"b1"}/>);
            details.push(AppUserStore.localizeStringCFL(i18n.FILE_NAME) + ': ' + this.props.fileName);
        } else if (this.props.version != undefined) {
            details.push(<br key={"b1"}/>);
            details.push(AppUserStore.localizeStringCFL(i18n.VERSION) + ': ' + this.props.version);
        }
        details.push(<br key={"b2"}/>);
        details.push(AppUserStore.localizeStringCFL(i18n.PROBES) + ' (' + this.props.probeNames.length + '):');
        return <div className="flex flex-11a flex-col confirm-panel">
            <div className="flex-none confirm-text">
                {details}
            </div>
            <div className="flex flex-11a flex-col confirm-probes-text">
                {_.chain(this.props.probeNames)
                   .sortBy((p)=>p.toUpperCase())
                   .map((name, id) => [<span key={id}>{name}</span>])
                   .flatten()
                   .value()}
            </div>
        </div>
    }
});

export default ProbeActionConfirmText;