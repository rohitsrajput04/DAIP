import React from 'react';
import { Outlet } from 'react-router-dom';
import Sidebar from './Sidebar';

function Layout() {
  return (
    <div className="d-flex">
      <Sidebar />
      <main className="daip-main flex-grow-1 p-4">
        <Outlet />
      </main>
    </div>
  );
}

export default Layout;
