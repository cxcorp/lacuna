import React, { Component } from 'react';
import BaseView from './BaseView';
import ProcessListContainer from '../components/ProcessListContainer';
import './Processes.css';

class Processes extends Component {
    render() {
        return (
            <BaseView title='Processes'>
                <ProcessListContainer />
            </BaseView>
        );
    }
}

export default Processes;