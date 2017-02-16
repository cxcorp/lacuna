import React from 'react';
import ReactDOM from 'react-dom';
import { Router, Route, IndexRoute, hashHistory } from 'react-router';
import App from './App';
import Processes from './views/Processes';
import Dashboard from './views/Dashboard';
import './index.css';

ReactDOM.render(
  <Router history={hashHistory}>
    <Route path='/' component={App}>
      <IndexRoute component={Dashboard} />
      <Route path='/processes' component={Processes} />
    </Route>
  </Router>,
  document.getElementById('root')
);
