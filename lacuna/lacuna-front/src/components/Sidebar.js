import React from 'react';
import { Link, IndexLink } from 'react-router';
import './Sidebar.css';

const Sidebar = () => (
    <nav className='sidebar'>
        <NavigationList>
            <NavigationSectionItem to='/' name='Dashboard'>
                <NavigationLinkItem to='/processes'>Processes</NavigationLinkItem>
                <NavigationLinkItem to='/memory'>Memory</NavigationLinkItem>
            </NavigationSectionItem>
        </NavigationList>
    </nav>
);

const NavigationList = ({children}) => (
    <ul className='navigation_list'>
        {children}
    </ul>
);

const NavigationSectionItem = ({to, name, children}) => (
    <li className='navigation_section_item'>
        <IndexLink to={to}
                   activeClassName='navigation_section_item__link--active_link'
                   className='navigation_section_item__link'
                   >
            <span>{name}</span>
        </IndexLink>
        <ul className='navigation_section_item__list'>
            {children}
        </ul>
    </li>
);

const NavigationLinkItem = ({to, children}) => (
    <li className='navigation_link_item'>
        <Link to={to}
              className='navigation_link_item__link'
              activeClassName='navigation_link_item__link--active_link'
              >
            <span>{children}</span>
        </Link>
    </li>
);


export default Sidebar;