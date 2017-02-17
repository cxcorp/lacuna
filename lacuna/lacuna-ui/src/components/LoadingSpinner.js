import React from 'react';
import './LoadingSpinner.css';

const LoadingSpinner = ({color, size}) => {
    const cubeStyle = {
        backgroundColor: color || '#1e1e1e1'
    };
    const cubeGridStyle = {
        width: size,
        height: size
    };
    const cubes = [...Array(9).keys()].map(id => (
        <div className={'sk-cube sk-cube' + (id + 1)} key={(id + 1)} style={cubeStyle}></div>
    ));
    return (
        // Originally by Tobias Ahlin, MIT license, https://github.com/tobiasahlin/SpinKit
        <div className='sk-cube-grid' style={cubeGridStyle}>
            {cubes}
        </div>
    );
};

export default LoadingSpinner;