import React from 'react';

const ProcessInfo = ({process}) => {
    return (
        <div>
            <p>Pid: {process.pid}</p>
            <p>Owner: {process.owner}</p>
            <p>Description: {process.description}</p>
        </div>
    );
};

export default ProcessInfo;