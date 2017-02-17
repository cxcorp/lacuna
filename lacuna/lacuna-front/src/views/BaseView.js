import React, { Component } from 'react';
import './BaseView.css';

class BaseView extends Component {
    render() {
        return (
            <div className='baseview'>
                <div className='baseview__title'>
                    <h1 className='baseview__title__text'>{this.props.title}</h1>
                </div>
                <div className='baseview__content'>
                    {this.props.children}
                </div>
            </div>
        );
    }
}

export default BaseView;