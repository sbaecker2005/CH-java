import React from 'react';
import { Outlet } from 'react-router-dom';
import SideBar from './SideBar';
import { useAuth } from '../contexts/AuthContext';
import NotificationBell from './NotificationBell';
import { useWebSocket } from '../hooks/useWebSocket';
import { Wifi, WifiOff } from 'lucide-react';
import type { NotificacaoResponse } from '../types';

interface LayoutProps {
  children?: React.ReactNode;
  title?: string;
}

const Layout: React.FC<LayoutProps> = ({ children, title }) => {
  const { user } = useAuth();
  const [lastNotification, setLastNotification] = React.useState<NotificacaoResponse | null>(null);

  const { connected } = useWebSocket(
    (n) => setLastNotification(n),
    undefined
  );

  return (
    <div className="flex min-h-screen bg-[#F8FAFC]">
      <SideBar />

      <div className="flex-1 ml-64 flex flex-col min-h-screen">
        {/* Header */}
        <header className="sticky top-0 z-30 bg-white border-b border-gray-100 shadow-sm">
          <div className="flex items-center justify-between px-8 py-4">
            <div>
              {title && <h1 className="text-xl font-bold text-gray-900">{title}</h1>}
            </div>
            <div className="flex items-center gap-4">
              {/* WebSocket status */}
              <div className={`flex items-center gap-1.5 text-xs font-medium px-3 py-1.5 rounded-full ${
                connected
                  ? 'bg-green-50 text-green-700'
                  : 'bg-gray-100 text-gray-500'
              }`}>
                {connected ? (
                  <>
                    <span className="w-1.5 h-1.5 bg-green-500 rounded-full animate-pulse" />
                    <Wifi className="w-3.5 h-3.5" />
                    <span>Conectado</span>
                  </>
                ) : (
                  <>
                    <WifiOff className="w-3.5 h-3.5" />
                    <span>Offline</span>
                  </>
                )}
              </div>

              <NotificationBell realtimeNotification={lastNotification} />

              <div className="flex items-center gap-3 pl-4 border-l border-gray-100">
                <div className="w-8 h-8 bg-teal-500 rounded-full flex items-center justify-center text-sm font-bold text-white">
                  {user?.nome?.charAt(0).toUpperCase() ?? 'U'}
                </div>
                <div className="hidden sm:block">
                  <p className="text-sm font-medium text-gray-900 leading-tight">{user?.nome}</p>
                  <p className="text-xs text-gray-500">{user?.role}</p>
                </div>
              </div>
            </div>
          </div>
        </header>

        {/* Main content */}
        <main className="flex-1 px-8 py-6">
          {children ?? <Outlet />}
        </main>
      </div>
    </div>
  );
};

export default Layout;
