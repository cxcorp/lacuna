import React, { Component } from 'react';
import { Link } from 'react-router';
import { Endpoints, Statuses } from '../data/Data';
import ProcessList from './ProcessList';
import Util from '../util/Util';

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
        let query = fetch(Endpoints.processes, {
            accept: 'application/json'
        }).then(response => {
            if (response.status !== 200) {
                // TODO: show error message to user
                console.error(response);
            }
            return response.json();
        }).then(data => {
            if (data.status === Statuses.success) { // inb4 antipattern
                this.setState({ processes: data.data });
            } else {
                // TODO: server gave errors, deal with it
                console.error(data);
            }
        });
    }

    render() {
        const linkGetter = pid => {
            const to = {
                pathname: 'memory',
                query: {
                    pid: pid
                }
            };
            return (<Link to={to}>{pid}</Link>);
        };
        return (
            <div>
                <ProcessList processes={this.state.processes}
                             pidMemoryPathGetter={linkGetter}
                             />
            </div>
        );
    }
}

export default ProcessListContainer;