import { useEffect, useRef, useState, useCallback } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import toast from 'react-hot-toast';
import type { NotificacaoResponse, LeadResponse } from '../types';

const USE_MOCK = true;

interface WebSocketHook {
  connected: boolean;
  lastNotification: NotificacaoResponse | null;
  lastUrgentLead: LeadResponse | null;
}

export const useWebSocket = (
  onNotification?: (n: NotificacaoResponse) => void,
  onUrgentLead?: (l: LeadResponse) => void
): WebSocketHook => {
  const [connected, setConnected] = useState(false);
  const [lastNotification, setLastNotification] = useState<NotificacaoResponse | null>(null);
  const [lastUrgentLead, setLastUrgentLead] = useState<LeadResponse | null>(null);
  const clientRef = useRef<Client | null>(null);

  const handleNotification = useCallback(
    (notification: NotificacaoResponse) => {
      setLastNotification(notification);
      if (onNotification) onNotification(notification);

      const icon = notification.geradoPorIa ? '✨' : '🔔';
      toast(
        `${icon} ${notification.mensagem}`,
        {
          duration: 5000,
          style: {
            background: notification.geradoPorIa ? '#1E40AF' : '#0F766E',
            color: 'white',
            borderRadius: '12px',
            padding: '12px 16px',
          },
        }
      );
    },
    [onNotification]
  );

  const handleUrgentLead = useCallback(
    (lead: LeadResponse) => {
      setLastUrgentLead(lead);
      if (onUrgentLead) onUrgentLead(lead);

      toast.error(
        `⚡ Lead URGENTE: ${lead.nome}`,
        {
          duration: 8000,
          style: {
            background: '#DC2626',
            color: 'white',
            borderRadius: '12px',
            padding: '12px 16px',
            fontWeight: 'bold',
          },
        }
      );
    },
    [onUrgentLead]
  );

  useEffect(() => {
    if (USE_MOCK) return;

    const token = localStorage.getItem('token');
    if (!token) return;

    const client = new Client({
      webSocketFactory: () => new SockJS('/ws'),
      connectHeaders: {
        Authorization: `Bearer ${token}`,
      },
      reconnectDelay: 5000,
      onConnect: () => {
        setConnected(true);

        client.subscribe('/topic/notificacoes', (message) => {
          try {
            const notification: NotificacaoResponse = JSON.parse(message.body);
            handleNotification(notification);
          } catch (e) {
            console.error('Failed to parse notification:', e);
          }
        });

        client.subscribe('/topic/leads/urgentes', (message) => {
          try {
            const lead: LeadResponse = JSON.parse(message.body);
            handleUrgentLead(lead);
          } catch (e) {
            console.error('Failed to parse urgent lead:', e);
          }
        });
      },
      onDisconnect: () => {
        setConnected(false);
      },
      onStompError: (frame) => {
        console.error('STOMP error:', frame);
        setConnected(false);
      },
    });

    client.activate();
    clientRef.current = client;

    return () => {
      client.deactivate();
      clientRef.current = null;
    };
  }, [handleNotification, handleUrgentLead]);

  return { connected, lastNotification, lastUrgentLead };
};
