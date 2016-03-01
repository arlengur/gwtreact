import React from 'react';

var TextInputTypeahead = React.createClass({

    getInitialState: function() {
        return {
            value: this.props.value || ''
        };
    },
    // Enable changing value via props
    componentWillReceiveProps: function(props) {
        this.setState({value: props.value});
    },
    render: function() {
        return <input className={this.props.className}
                      style={this.props.style}
                      disabled={this.props.disabled}
                      id={this.props.id}
                      placeholder={this.props.placeholder}
                      onChange={this._onChange}
                      onBlur={(event) => {if(typeof this.props.onBlur == 'function') this.props.onBlur(event.target.value)} }
                      onKeyPress={this.props.onKeyPress}
                      value={this.state.value}
                      autoFocus={true}/>
    },
    _onChange: function(event) {
        if(typeof this.props.validate != 'undefined' &&
           !this.props.validate(event.target.value)){
            this.setState({
                value: this.state.value
            })
        } else {
            if(typeof this.props.onChange == 'function') {
                this.props.onChange(event.target.value);
            }
            this.setState({
                value: event.target.value
            });
        }
    }
});

export default TextInputTypeahead;
