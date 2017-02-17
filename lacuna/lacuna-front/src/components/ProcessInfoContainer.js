import React, { Component } from 'react';
import { Endpoints, Statuses } from '../data/Data';
import LoadingDots from './LoadingDots';
import ProcessInfo from './ProcessInfo';

class ProcessInfoContainer extends Component {
    constructor(props) {
        super(props);

        this.state = { process: null };
        this.fetchProcess.bind(this);
    }

    componentDidMount() {
        this.fetchProcess();
    }

    fetchProcess() {
        fetch(Endpoints.processes + '/' + this.props.pid, {
            accept: 'application/json'
        }).then(response => {
            if (response.status !== 200) {
                // TODO: show error message to user
                console.error(response);
            }
            return response.json();
        }).then(data => {
            if (data.status === Statuses.success) {
                this.setState({ process: data.data });
            } else {
                // TODO: server gave errors, deal with it
                console.error(data);
            }
        });
    }

    render() {
        if (!this.state.process) {
            return (
                <div>Loading process data<LoadingDots timeout={500} dotCount={4} /></div>
            );
        }
        return (
            <div>
                <ProcessInfo process={this.state.process}/>
            </div>
        );
    }
}

export default ProcessInfoContainer;