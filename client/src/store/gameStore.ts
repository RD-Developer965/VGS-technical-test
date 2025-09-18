import { create } from 'zustand';
import type { Player } from '../types/game';

interface GameState {
  selectedPlayer: Player | null;
  setSelectedPlayer: (player: Player) => void;
  currentGameId: number | null;
  setCurrentGameId: (id: number | null) => void;
}

export const useGameStore = create<GameState>((set) => ({
  selectedPlayer: null,
  setSelectedPlayer: (player) => set({ selectedPlayer: player }),
  currentGameId: null,
  setCurrentGameId: (id) => set({ currentGameId: id }),
}));