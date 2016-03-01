import hljs from 'highlight.js';
import React from 'react';

var XmlHighlight = React.createClass({
    componentDidMount: function () {
        this.highlightCode();
    },
    componentDidUpdate: function () {
        this.highlightCode();
    },
    highlightCode: function () {
        hljs.highlightBlock(React.findDOMNode(this.refs.config));
    },
    render: function () {
        return <pre className='highlight-pre'>
            <code ref='config' className='xml'>{this.props.text}</code>
        </pre>;
    }
});

export default XmlHighlight;
