import React, { Component } from 'react';
import BaseView from './BaseView';
import { ErrorBox, WarningBox, InfoBox } from '../components/NotificationBox';
import LoadingSpinner from '../components/LoadingSpinner';

class Dashboard extends Component {
    render() {
        return (
            <BaseView title='Dashboard'>
                <div>
                    <ErrorBox>Error! Unable to connect to lacuna!</ErrorBox>
                    <WarningBox>Warning! Experiencing high latency!</WarningBox>
                    <InfoBox>Tip! Enter your target offset here and fire away!</InfoBox>
                </div>
            </BaseView>
        );
    }
}

export default Dashboard;