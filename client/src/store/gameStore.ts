import { create } from 'zustand';

interface GameState {
  currentGameId: number | null;
  setCurrentGameId: (id: number | null) => void;
  currentPlayer: 'X' | 'O';
  setCurrentPlayer: (player: 'X' | 'O') => void;
}

export const useGameStore = create<GameState>((set) => ({
  currentPlayer: 'X',
  setCurrentPlayer: (player) => set({ currentPlayer: player }),
  currentGameId: null,
  setCurrentGameId: (id) => set({ currentGameId: id }),
}));