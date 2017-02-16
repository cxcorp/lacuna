import React from 'react';
import { IndexLink } from 'react-router';
import './Header.css';

const Header = () => (
    <header className='navigation'>
        <div className='navigation__content'>
            <p className='navigation__title'>
                <IndexLink to='/' title='Dashboard' className='navigation__title__link'>lacuna</IndexLink>
            </p>
        </div>
    </header>
);

export default Header;