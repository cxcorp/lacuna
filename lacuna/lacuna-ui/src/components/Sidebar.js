import React from 'react';
import { Link, IndexLink } from 'react-router';
import './Sidebar.css';

const Sidebar = () => (
    <div className='sidebar__nav'>
        <NavigationList>
            <NavigationLink to='/' isIndex={true}>Dashboard</NavigationLink>
            <NavigationLink to='/processes'>Processes</NavigationLink>
        </NavigationList>
    </div>
);

const NavigationList = ({children}) => (
    <ul className='navigationlist'>
        {children}
    </ul>
);

const NavigationLink = ({to, children, isIndex}) => (
    <li className='navigationlist__link'>
        {isIndex
            ? <IndexLink to={to} activeClassName='active_link'><span>{children}</span></IndexLink>
            : <Link to={to} activeClassName='active_link'><span>{children}</span></Link>}
    </li>
);


export default Sidebar;