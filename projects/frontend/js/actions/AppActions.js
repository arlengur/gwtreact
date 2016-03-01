import Misc from "util/Misc";
import AppConstants from '../constants/AppConstants';
import AppDispatcher from '../dispatcher/AppDispatcher';

var AppActions = Object.freeze({
    logout: () => {
        var url = "j_spring_security_logout";
        $.ajax({
            url: url,
            method: 'POST',
            success: function() {
                location.reload(true);
            }.bind(this),
            error: function(xhr, status, err) {
                console.error(url, status, err.toString());
                Misc.refreshIfForbidden(xhr);
            }.bind(this)
        });
    },
    showConfirmModal: (title, content, closeAction, confirm, reject) => {
        AppDispatcher.dispatch({
            actionType: AppConstants.CONFIRM_MODAL_SHOW,
            title: title,
            content: content,
            closeAction: closeAction,
            onConfirm: confirm,
            onReject: reject
        });
    },
    closeErrorModal: () => {
        AppDispatcher.dispatch({
            actionType: AppConstants.ERROR_MODAL_HIDE
        });
    }
});

export default AppActions;