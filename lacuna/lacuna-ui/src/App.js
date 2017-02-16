import React, { Component } from 'react';
import logo from './logo.svg';
import Navigation from './Navigation';
import './styles/App.css';

class App extends Component {
  render() {
    return (
      <div className='app'>
        <Navigation />
        <div className='app__container'>
          {this.props.children}
        </div>
      </div>
    );
  }
}

export default App;
