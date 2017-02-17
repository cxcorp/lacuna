import React, { Component } from 'react';
import BaseView from './BaseView';
import ProcessListContainer from '../components/ProcessListContainer';
import './Processes.css';

class Processes extends Component {
    render() {
        return (
            <BaseView title='Processes'>
                <p className='processes__subtitle'>Lorem ipsum dolor sit amet, consectetur adipiscing elit.</p>
                <ProcessListContainer />
            </BaseView>
        );
    }
}

export default Processes;