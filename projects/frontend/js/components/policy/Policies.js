import React from 'react';
import NavMenu from '../../components/common/NavMenu';
import PolicyGrid from './PolicyGrid';
import PolicyCreate from './PolicyCreate';
import {default as Store, DIALOG_STATE as STATE} from '../../stores/PolicyStore';

var Policies = React.createClass({
    componentDidMount: function() {
        Store.addChangeListener(this._onChange);
    },
    componentWillUnmount: function() {
        Store.removeChangeListener(this._onChange);
    },
    getInitialState: function() {
        return {store: Store.getState()};
    },
    _onChange: function() {
        this.setState({store: Store.getState()});
    },

    render: function() {
        var store = this.state.store;
        return <div className="probe-view container-fluid">
            <NavMenu className="row" title="Policies"/>
            <PolicyGrid/>
            {store.get('createActive') ?
                <PolicyCreate store={store}/>
                : ""}
        </div>
    }
});

export default Policies;