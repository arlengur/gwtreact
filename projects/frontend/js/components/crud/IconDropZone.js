import React from 'react';
import Dropzone from 'react-dropzone';
import CrudActions from '../../actions/ChannelCrudActions';
import Store from '../../stores/ChannelCrudStore';
import Misc from '../../util/Misc';

var IconDropZone = React.createClass({
    onDrop: function (res) {
        const file = res[0];
        console.log('Received files: ', file);
        CrudActions.newImageSelected(file);
    },

    render: function () {
        return (
             <Dropzone onDrop={this.onDrop} className="pull-left crud-channel-dropzone" size={78}>
                  <img src={this.props.logo} className="crud-channel-image"/>
             </Dropzone>
        );
    }
});

export default IconDropZone;
