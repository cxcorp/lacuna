import React, { Component } from 'react';
import Header from './components/Header';
import Sidebar from './components/Sidebar';
import './App.css';

class App extends Component {
  render() {
    return (
      <div className='app'>
        <Header />
        <Sidebar />
        <div className='app__container'>
          {this.props.children}
        </div>
      </div>
    );
  }
}

export default App;
