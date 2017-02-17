import React, { Component } from 'react';
import BaseView from './BaseView';
import ProcessInfoContainer from '../components/ProcessInfoContainer';
import MemoryReaderContainer from '../components/MemoryReaderContainer';

class Memory extends Component {
    render() {
        if (!this.isPidPresent()) {
            return (
                <BaseView title='Process Memory'>
                    No process selected
                </BaseView>
            )
        }
        return (
            <BaseView title='Process Memory'>
                <ProcessInfoContainer pid={this.props.location.query.pid} />
                <MemoryReaderContainer pid={this.props.location.query.pid} />
            </BaseView>
        );
    }

    isPidPresent() {
        return this.props.location
            && this.props.location.query
            && (typeof this.props.location.query.pid === 'string'
             || typeof this.props.location.query.pid === 'number');
    }
}

export default Memory;