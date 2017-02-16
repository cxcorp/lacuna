import React, { Component } from 'react';
import { Link, IndexLink } from 'react-router';
import './styles/Navigation.css';

const Navigation = () => (
    <header className='navigation'>
        <div className='navigation__content'>
            <p className='navigation__title'>lacuna</p>
            <div className='navigation__nav'>
                <ul className='navigation__nav__list'>
                    <li className='navigation__nav__list__item'>
                        <IndexLink to='/' activeClassName='active_link'><span>dashboard</span></IndexLink>
                    </li>
                    <li className='navigation__nav__list__item'>
                        <Link to='/processes' activeClassName='active_link'><span>processes</span></Link>
                    </li>
                </ul>
            </div>
        </div>
    </header>
);

export default Navigation;