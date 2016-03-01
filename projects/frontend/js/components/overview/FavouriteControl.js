import React from 'react';
import OverviewActions from '../../actions/OverviewActions';
import Images from '../../util/Images';

var FavouriteControl = React.createClass({
    render: function () {
        return (
            <div className="view-icon pull-right clickable"
                onClick={function () {
                    OverviewActions.setFavourite(this.props.channelId, !this.props.enabled);
                }.bind(this)}>
                <img src={Images.favouriteIcon(this.props.enabled, false)}/>
            </div>
        );
    }
});

export default FavouriteControl;
