import { useQuery, useMutation } from '@tanstack/react-query';
import { useGameStore } from '../store/gameStore';
import type { Game, Player, MoveRequest } from '../types/game';
import axios from 'axios';

const API_URL = 'http://localhost:8080/api/games';

async function createGame() {
  const response = await axios.post<Game>(`${API_URL}/create`);
  return response.data;
}

async function makeMove(move: MoveRequest) {
  const response = await axios.post<Game>(`${API_URL}/move`, move);
  return response.data;
}

async function getGameStatus(gameId: number) {
  const response = await axios.get<Game>(`${API_URL}/status?matchId=${gameId}`);
  return response.data;
}

function TicTacToe() {
  const { selectedPlayer, setSelectedPlayer, currentGameId, setCurrentGameId } = useGameStore();

  const { data: gameStatus, refetch: refetchStatus } = useQuery({
    queryKey: ['gameStatus', currentGameId],
    queryFn: () => currentGameId ? getGameStatus(currentGameId) : null,
    enabled: !!currentGameId,
  });

  const createGameMutation = useMutation({
    mutationFn: createGame,
    onSuccess: (game) => {
      setCurrentGameId(game.id);
    },
  });

  const makeMoveMutation = useMutation({
    mutationFn: makeMove,
    onSuccess: () => {
      refetchStatus();
    },
  });

  const handleCellClick = (x: number, y: number) => {
    if (!currentGameId || !selectedPlayer || !gameStatus) return;

    if (gameStatus.currentPlayer !== selectedPlayer) {
      alert("It's not your turn!");
      return;
    }

    makeMoveMutation.mutate({
      matchId: currentGameId,
      playerId: selectedPlayer,
      square: { x: x + 1, y: y + 1 },
    });
  };

  const handlePlayerSelect = (player: Player) => {
    setSelectedPlayer(player);
    createGameMutation.mutate();
  };

  if (!selectedPlayer) {
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
            className="transform transition-all duration-200 hover:scale-105 bg-primary-500 text-white px-8 py-3 rounded-lg font-semibold shadow-lg hover:bg-primary-600 hover:shadow-xl flex items-center justify-center space-x-2 min-w-[140px]"
          >
            <span className="text-xl">Play as X</span>
          </button>
          <button
            onClick={() => handlePlayerSelect('O')}
            className="transform transition-all duration-200 hover:scale-105 bg-secondary-500 text-white px-8 py-3 rounded-lg font-semibold shadow-lg hover:bg-secondary-600 hover:shadow-xl flex items-center justify-center space-x-2 min-w-[140px]"
          >
            <span className="text-xl">Play as O</span>
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="bg-white p-10 rounded-xl shadow-2xl backdrop-blur-sm bg-white/90 max-w-lg mx-auto">
      <div className="flex justify-between items-center mb-8">
        <h1 className="text-3xl font-bold text-gray-800 tracking-tight">Tic Tac Toe</h1>
        <div className="px-4 py-2 bg-primary-100 rounded-lg">
          <span className="text-primary-700 font-semibold">
            Playing as {selectedPlayer}
          </span>
        </div>
      </div>
      
      <div className="grid grid-cols-3 gap-4 mb-8">
        {gameStatus?.board.map((row, i) =>
          row.map((cell, j) => (
            <button
              key={`${i}-${j}`}
              onClick={() => handleCellClick(i, j)}
              disabled={!!cell || !!gameStatus?.isFinished}
              className={`w-24 h-24 text-4xl font-bold flex items-center justify-center rounded-xl transform transition-all duration-200 
                ${!cell && !gameStatus?.isFinished ? 'hover:scale-105 hover:shadow-lg' : ''}
                ${cell === 'X' ? 'bg-primary-100 text-primary-600' : 
                  cell === 'O' ? 'bg-secondary-100 text-secondary-600' : 
                  'bg-gray-50 hover:bg-gray-100'}`}
            >
              {cell}
            </button>
          ))
        )}
      </div>

      {gameStatus?.winner && (
        <div className="bg-accent-100 text-accent-700 p-4 rounded-lg text-xl font-bold text-center mb-6">
          {gameStatus.winner === 'DRAW' 
            ? "It's a draw!" 
            : `Player ${gameStatus.winner} wins! 🎉`}
        </div>
      )}

      <div className="bg-gray-50 rounded-lg p-4">
        <div className="text-center">
          {!gameStatus?.isFinished && (
            <div className="flex items-center justify-center space-x-2">
              <div className={`w-3 h-3 rounded-full ${gameStatus?.currentPlayer === 'X' ? 'bg-primary-500' : 'bg-secondary-500'} animate-pulse`}></div>
              <span className="text-gray-700 font-medium">
                Current turn: {gameStatus?.currentPlayer}
              </span>
            </div>
          )}
          {gameStatus?.isFinished && (
            <span className="text-gray-700 font-medium">Game Over</span>
          )}
        </div>
      </div>
    </div>
  );
}

export default TicTacToe;