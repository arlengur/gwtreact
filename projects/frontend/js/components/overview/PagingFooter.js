import React from 'react';
import Actions from '../../actions/OverviewActions';
import Input from '../../components/common/TextInputTypeahead';
import Misc from '../../util/Misc';
import i18n from '../../constants/i18nConstants';
import AppSettingsStore from '../../stores/AppUserSettingsStore';
const localize = AppSettingsStore.localizeString;

var PagingFooter = React.createClass({
    render: function() {
        var p = this.props;
        var pageNum =  Math.ceil(p.channelNum/p.perPage);
        var pagingStatus = ""+((p.currentPage-1)*p.perPage+1)+
            "-"+(p.currentPage==pageNum ? p.channelNum : (p.currentPage*p.perPage))+
            "/"+p.channelNum;
        return <div className="channels-footer flex-none container-fluid override-padding-0">
            <div className="page-first pull-left override-margin-7-5"
                onClick={()=>{Actions.setPage(1)}}/>
            <div className="page-prev pull-left override-margin-7-5"
                onClick={()=>{if(p.currentPage!=1)Actions.setPage(p.currentPage-1)}}/>
            <label className="small-text channels-footer-text pull-left" htmlFor="page">{localize(i18n.CHANNELS_FOOTER_PAGE)}</label>
            <Input type="text" className="page-input small-text pull-left" id="page" value={p.currentPage}
                   placeholder={1}
                   onKeyPress={Misc.validateDigit}
                   validate={(val) => Misc.validateNumber(val, pageNum, 1)}
                   onChange={(val) => val=='' ? 1 : Actions.setPage(parseInt(val))}/>
            <span className="small-text channels-footer-text pull-left">{localize(i18n.CHANNELS_FOOTER_OF)+" "+pageNum}</span>
            <div className="page-next pull-left override-margin-7-5"
                 onClick={()=>{if(p.currentPage!=pageNum)Actions.setPage(p.currentPage+1)}}/>
            <div className="page-last pull-left override-margin-7-5"
                 onClick={()=>{Actions.setPage(pageNum)}}/>
            <span className="small-text channels-footer-text pull-right">{pagingStatus}</span>
        </div>
    }
});

export default PagingFooter;
