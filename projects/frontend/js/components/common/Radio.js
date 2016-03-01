import React from 'react';

var Radio = React.createClass({
    _onChange: function(){
        if(typeof this.props.onChange == 'function') this.props.onChange();
    },
    render: function () {
        const clazz = this.props.className + (this.props.disabled != null ? ' radio-input-locked' : ' radio-input');
        return <div className="pull-left">
            <input className='hidden' id={this.props.id} type='radio' checked={this.props.checked} onChange={this._onChange}/>
                <label className={clazz} htmlFor={this.props.id} style={this.props.style}/>
        </div>
    }
});

export default Radio;