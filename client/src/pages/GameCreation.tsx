import { useNavigate } from 'react-router-dom';
import { useMutation } from '@tanstack/react-query';
import { useGameStore } from '../store/gameStore';
import { toast } from 'sonner';
import axios from 'axios';
import type { Game } from '../types/game';
import { config } from '../config/env';

async function createGame() {
  const response = await axios.post<Game>(`${config.apiUrl}/games/create`);
  return response.data;
}

function GameCreation() {
  const navigate = useNavigate();
  const { setCurrentGameId } = useGameStore();

  const createGameMutation = useMutation({
    mutationFn: createGame,
    onSuccess: (game) => {
      setCurrentGameId(game.id);
      navigate(`/game/${game.id}`);
    },
    onError: (error) => {
      if (axios.isAxiosError(error)) {
        toast.error(error.response?.data?.message || 'Error creating game');
      } else {
        toast.error('Something went wrong');
      }
    }
  });

  const handleNewGame = () => {
    createGameMutation.mutate();
  };

  return (
    <div className="bg-white p-10 rounded-xl shadow-2xl backdrop-blur-sm bg-white/90 max-w-md mx-auto">
      <h1 className="text-3xl font-bold mb-6 text-gray-800 text-center tracking-tight">
        Welcome to Tic Tac Toe
      </h1>
      <p className="text-gray-600 mb-8 text-center">
        Click the button below to start a new game!
      </p>
      <div className="flex justify-center">
        <button
          onClick={() => handleNewGame()}
          disabled={createGameMutation.isPending}
          className={`appearance-none border-0 transform transition-all duration-200 hover:scale-105 bg-gray-900 text-white px-8 py-3 rounded-lg font-semibold shadow-lg hover:bg-gray-800 hover:shadow-xl focus:outline-none focus:ring-2 focus:ring-gray-700 focus:ring-opacity-50 flex items-center justify-center space-x-2 min-w-[140px] disabled:opacity-50 disabled:cursor-not-allowed ${
            createGameMutation.isPending ? 'opacity-50 cursor-not-allowed' : ''
          }`}
        >
          <span className="text-xl">
            {createGameMutation.isPending ? 'Creating...' : 'New Game'}
          </span>
        </button>
      </div>
    </div>
  );
}

export default GameCreation;