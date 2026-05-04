import React from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import {
  LayoutDashboard,
  Users,
  Calendar,
  Bell,
  Settings,
  LogOut,
  Cross,
  Activity,
} from 'lucide-react';
import { useAuth } from '../contexts/AuthContext';

const navItems = [
  { to: '/', icon: LayoutDashboard, label: 'Dashboard' },
  { to: '/leads', icon: Users, label: 'Leads' },
  { to: '/agendamentos', icon: Calendar, label: 'Agendamentos' },
  { to: '/notificacoes', icon: Bell, label: 'Notificações' },
  { to: '/configuracoes', icon: Settings, label: 'Configurações' },
];

const SideBar: React.FC = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <aside className="fixed left-0 top-0 h-full w-64 bg-primary-900 text-white flex flex-col z-40 shadow-2xl">
      {/* Logo */}
      <div className="px-6 py-6 border-b border-primary-800">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 bg-white rounded-xl flex items-center justify-center flex-shrink-0">
            <Cross className="w-5 h-5 text-primary-800" />
          </div>
          <div>
            <p className="font-bold text-sm leading-tight">Hospital São Rafael</p>
            <p className="text-primary-300 text-xs">Sistema CRM</p>
          </div>
        </div>
      </div>

      {/* Nav */}
      <nav className="flex-1 px-3 py-4 space-y-1">
        {navItems.map(({ to, icon: Icon, label }) => (
          <NavLink
            key={to}
            to={to}
            end={to === '/'}
            className={({ isActive }) =>
              `flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-medium transition-all duration-200 group ${
                isActive
                  ? 'bg-primary-700 text-white shadow-lg'
                  : 'text-primary-200 hover:bg-primary-800 hover:text-white'
              }`
            }
          >
            <Icon className="w-5 h-5 flex-shrink-0" />
            {label}
          </NavLink>
        ))}
      </nav>

      {/* Status indicator */}
      <div className="px-4 py-3 border-t border-primary-800">
        <div className="flex items-center gap-2 text-xs text-primary-300">
          <Activity className="w-3.5 h-3.5" />
          <span>Sistema Online</span>
          <span className="ml-auto w-2 h-2 bg-green-400 rounded-full animate-pulse" />
        </div>
      </div>

      {/* User */}
      <div className="px-4 pb-4">
        <div className="bg-primary-800 rounded-xl p-3">
          <div className="flex items-center gap-3 mb-3">
            <div className="w-8 h-8 bg-teal-500 rounded-full flex items-center justify-center text-xs font-bold text-white flex-shrink-0">
              {user?.nome?.charAt(0).toUpperCase() ?? 'U'}
            </div>
            <div className="overflow-hidden">
              <p className="text-sm font-medium text-white truncate">{user?.nome ?? 'Usuário'}</p>
              <p className="text-xs text-primary-300 truncate">{user?.role ?? ''}</p>
            </div>
          </div>
          <button
            onClick={handleLogout}
            className="w-full flex items-center justify-center gap-2 text-xs text-primary-300 hover:text-red-400 transition-colors duration-200 py-1"
          >
            <LogOut className="w-3.5 h-3.5" />
            Sair do sistema
          </button>
        </div>
      </div>
    </aside>
  );
};

export default SideBar;
