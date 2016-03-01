import React from 'react';

var LoadingProgressBar = React.createClass({
    render: function () {
        if (this.props.dataLoaded == true) {
            return <div className="modal hide"></div>
        } else {
            return (
                <div className="modal show">
                    <div className="grey-page"></div>
                    <div className="modal-dialog modal-dialog-loader">
                        <div className="modal-content">
                            <div className="modal-body modal-body-loader">
                                <img src="img/preloader.gif" />
                            </div>
                        </div>
                    </div>
                </div>
            );
        }
    }
});

export default LoadingProgressBar;
