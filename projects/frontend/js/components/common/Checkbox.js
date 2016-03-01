import React from 'react';

var Checkbox = React.createClass({
    _onChange: function(){
        if(typeof this.props.onChange == 'function') this.props.onChange();
    },
    render: function () {
        return <div className="pull-left">
            <input className='hidden' id={this.props.id} type='checkbox' checked={this.props.checked} onChange={this._onChange}/>
                <label className={this.props.className + ' checkbox-input'} htmlFor={this.props.id} style={this.props.style}/>
        </div>
    }
});

export default Checkbox;