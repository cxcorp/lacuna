import React, { Component } from 'react';
import { Endpoints, Statuses } from '../data/Data';
import ProcessList from './ProcessList';

class ProcessListContainer extends Component {
    constructor(props) {
        super(props);

        this.state = { processes: null };
        this.fetchProcesses.bind(this);
    }

    componentDidMount() {
        this.fetchProcesses();
    }

    fetchProcesses() {
        fetch(Endpoints.processes, {
            accept: 'application/json'
        }).then(response => {
            if (response.status !== 200) {
                // TODO: show error message to user
                console.error(response);
            }
            return response.json();
        }).then(data => {
            if (data.status === Statuses.success) {
                this.setState({ processes: data.data });
            } else {
                // TODO: server gave errors, deal with it
                console.error(data);
            }
        });
    }

    render() {
        return (
            <div>
                <ProcessList processes={this.state.processes} />
            </div>
        );
    }
}

export default ProcessListContainer;