import React from 'react';
import ReactDOM from 'react-dom';
import { Router, Route, IndexRoute, hashHistory } from 'react-router';
import App from './App';
import Dashboard from './views/Dashboard';
import Processes from './views/Processes';
import Memory from './views/Memory';
import './index.css';

ReactDOM.render(
  <Router history={hashHistory}>
    <Route name='index' path='/' component={App}>
      <IndexRoute component={Dashboard} />
      <Route name='processes' path='/processes' component={Processes} />
      <Route name='memory' path='/memory' component={Memory} />
    </Route>
  </Router>,
  document.getElementById('root')
);
