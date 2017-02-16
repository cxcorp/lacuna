import React from 'react';
import ReactDOM from 'react-dom';
import { Router, Route, IndexRoute, hashHistory } from 'react-router';
import App from './App';
import Processes from './routes/Processes'
import './styles/index.css';

ReactDOM.render(
  <Router history={hashHistory}>
    <Route path='/' component={App}>
      <Route path='/processes' component={Processes} />
    </Route>
  </Router>,
  document.getElementById('root')
);
