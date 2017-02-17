import React from 'react';
import './NotificationBox.css';

const NotificationBox = ({color, symbol, children}) => {
    const iconStyle = {
        backgroundColor: color
    };

    return (
        <div className='notification_box'>
            <div className='notification_box__icon' style={iconStyle}>
                <p className='notification_box__icon__symbol'>{symbol}</p>
            </div>
            <p className='notification_box__content'>{children}</p>
        </div>
    );
};

const ErrorBox = ({children}) => new NotificationBox({ color: '#d35400', symbol: '‼', children: children});
const WarningBox = ({children}) => new NotificationBox({ color: '#f1c40f', symbol: '!', children: children});
const InfoBox = ({children}) => new NotificationBox({ color: '#9b59b6', symbol: 'ℹ', children: children});

export { ErrorBox, WarningBox, InfoBox };