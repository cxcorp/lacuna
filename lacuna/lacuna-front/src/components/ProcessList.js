import React from 'react';
import { InfoBox } from './NotificationBox';
import LoadingDots from './LoadingDots';
import './ProcessList.css';

const ProcessList = ({processes, pidMemoryPathGetter}) => {
    if (!processes) {
        return (
            <InfoBox className='process_list__loading'>
                Loading processes
                <LoadingDots dotCount={4} timeout={500} />
            </InfoBox>
        );
    }
    console.group('items');
    const tableRows = processes.map(p => <ProcessListItem key={p.pid} process={p} linkGetter={pidMemoryPathGetter} />);
    console.groupEnd();
    tableRows.reverse();
    return (
        <div>
            <InfoBox>{tableRows.length} processes fetched!</InfoBox>
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
        </table></div>
    );
};

const ProcessListItem = ({process, linkGetter}) => {
    return (
    <tr className='process_list_item'>
        <td>{linkGetter(process.pid)}</td>
        <td>{process.owner}</td>
        <td>{process.description}</td>
    </tr>
)};

export default ProcessList;