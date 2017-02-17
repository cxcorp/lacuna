import React, { Component } from 'react';
import { InfoBox } from './NotificationBox';
import LoadingDots from './LoadingDots';
import './ProcessList.css';

const ProcessList = ({processes}) => {
    if (!processes) {
        return (
            <InfoBox className='process_list__loading'>
                Loading processes
                <LoadingDots dotCount={4} timeout={500} />
            </InfoBox>
        );
    }

    const tableRows = processes.map(p => <ProcessListItem key={p.pid} process={p} />);
    tableRows.reverse();
    return (
        <table className='process_list'>
            <colgroup span="3"></colgroup>
            <thead>
                <tr className='process_list__header_row'>
                    <th>PID</th>
                    <th>Owner</th>
                    <th>Description</th>
                </tr>
            </thead>
            <tbody>
                {tableRows}
            </tbody>
        </table>
    );
};

const ProcessListItem = ({process}) => (
    <tr className='process_list_item'>
        <td>{process.pid}</td>
        <td>{process.owner}</td>
        <td>{process.description}</td>
    </tr>
);

export default ProcessList;