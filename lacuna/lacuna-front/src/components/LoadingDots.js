import React, { Component } from 'react';

class LoadingDots extends Component {
    constructor(props) {
        super(props);
        this.state = {
            intervalId: 0,
            dots: 1
        };
        this.updateDots = this.updateDots.bind(this);
    }

    updateDots() {
        this.setState({
            dots: (this.state.dots + 1) % this.props.dotCount
        });
    }

    componentDidMount() {
        this.setState({
            intervalId: setInterval(this.updateDots, this.props.timeout)
        });
    }

    componentWillUnmount() {
        clearInterval(this.state.intervalId);
    }

    render() {
        return (
            <span>{Array(this.state.dots + 1).join('.')}</span>
        );
    }
}

export default LoadingDots;