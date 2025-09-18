import { useNavigate } from 'react-router-dom';
import { useMutation } from '@tanstack/react-query';
import { useGameStore } from '../store/gameStore';
import { toast } from 'sonner';
import axios from 'axios';
import type { Game, Player } from '../types/game';

const API_URL = 'http://localhost:8080/api/games';

async function createGame() {
  const response = await axios.post<Game>(`${API_URL}/create`);
  return response.data;
}

function GameCreation() {
  const navigate = useNavigate();
  const { setSelectedPlayer, setCurrentGameId } = useGameStore();

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

  const handlePlayerSelect = (player: Player) => {
    setSelectedPlayer(player);
    createGameMutation.mutate();
  };

  return (
    <div className="bg-white p-10 rounded-xl shadow-2xl backdrop-blur-sm bg-white/90 max-w-md mx-auto">
      <h1 className="text-3xl font-bold mb-6 text-gray-800 text-center tracking-tight">
        Welcome to Tic Tac Toe
      </h1>
      <p className="text-gray-600 mb-8 text-center">
        Choose your player to start the game
      </p>
      <div className="flex gap-6 justify-center">
        <button
          onClick={() => handlePlayerSelect('X')}
          disabled={createGameMutation.isPending}
          className={`transform transition-all duration-200 hover:scale-105 bg-primary-500 text-white px-8 py-3 rounded-lg font-semibold shadow-lg hover:bg-primary-600 hover:shadow-xl flex items-center justify-center space-x-2 min-w-[140px] ${
            createGameMutation.isPending ? 'opacity-50 cursor-not-allowed' : ''
          }`}
        >
          <span className="text-xl">
            {createGameMutation.isPending ? 'Creating...' : 'Play as X'}
          </span>
        </button>
        <button
          onClick={() => handlePlayerSelect('O')}
          disabled={createGameMutation.isPending}
          className={`transform transition-all duration-200 hover:scale-105 bg-secondary-500 text-white px-8 py-3 rounded-lg font-semibold shadow-lg hover:bg-secondary-600 hover:shadow-xl flex items-center justify-center space-x-2 min-w-[140px] ${
            createGameMutation.isPending ? 'opacity-50 cursor-not-allowed' : ''
          }`}
        >
          <span className="text-xl">
            {createGameMutation.isPending ? 'Creating...' : 'Play as O'}
          </span>
        </button>
      </div>
    </div>
  );
}

export default GameCreation;