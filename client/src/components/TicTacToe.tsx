import { useQuery, useMutation } from '@tanstack/react-query';
import { useGameStore } from '../store/gameStore';
import { type Game, type Player, type MoveRequest, GameStatus, CellValue } from '../types/game';
import axios from 'axios';
import { config } from '../config/env';

async function createGame() {
  const response = await axios.post<Game>(`${config.apiUrl}/games/create`);
  return response.data;
}

async function makeMove(move: MoveRequest) {
  const response = await axios.post<Game>(`${config.apiUrl}/games/move`, move);
  return response.data;
}

async function getGameStatus(gameId: number) {
  const response = await axios.get<Game>(`${config.apiUrl}/games/status?matchId=${gameId}`);
  return response.data;
}

export default function TicTacToe() {
  const { currentPlayer, setCurrentPlayer, currentGameId, setCurrentGameId } = useGameStore();

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
    if (!currentGameId || !currentPlayer || !gameStatus) return;

    if (gameStatus.currentTurn !== currentPlayer) {
      alert("It's not your turn!");
      return;
    }

    makeMoveMutation.mutate({
      matchId: currentGameId,
      playerId: currentPlayer,
      square: { x: x + 1, y: y + 1 },
    });
  };

  const handlePlayerSelect = (player: Player) => {
    setCurrentPlayer(player);
    createGameMutation.mutate();
  };

  if (!currentPlayer) {
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

  if (!gameStatus) {
    return (
      <div className="text-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-500 mx-auto"></div>
        <p className="mt-4 text-gray-600">Loading game...</p>
      </div>
    );
  }

  return (
    <div className="bg-white p-10 rounded-xl shadow-2xl backdrop-blur-sm bg-white/90 max-w-lg mx-auto">
      <div className="flex justify-between items-center mb-8">
        <h1 className="text-3xl font-bold text-gray-800 tracking-tight">Tic Tac Toe</h1>
        <div className="px-4 py-2 bg-primary-100 rounded-lg">
          <span className="text-primary-700 font-semibold">
            Playing as {currentPlayer}
          </span>
        </div>
      </div>
      
      <div className="grid grid-cols-3 gap-4 mb-8">
        {gameStatus?.board.map((cell) => (
          <button
            key={`${cell.row}-${cell.column}`}
            onClick={() => handleCellClick(cell.row - 1, cell.column - 1)}
            disabled={cell.value !== CellValue.EMPTY}
            className={`w-24 h-24 text-4xl font-bold flex items-center justify-center rounded-xl transform transition-all duration-200 
              ${cell.value === CellValue.EMPTY ? 'hover:scale-105 hover:shadow-lg' : ''}
              ${cell.value === 'X' ? 'bg-primary-100 text-primary-600' : 
                cell.value === 'O' ? 'bg-secondary-100 text-secondary-600' : 
                'bg-gray-50 hover:bg-gray-100'}`}
          >
            {cell.value === CellValue.EMPTY ? '' : cell.value}
          </button>
        ))}
      </div>

      <div className="bg-gray-50 rounded-lg p-4">
        <div className="text-center">
          {gameStatus?.status === 'IN_PROGRESS' && (
            <div className="flex items-center justify-center space-x-2">
              <div className={`w-3 h-3 rounded-full ${gameStatus?.currentTurn === 'X' ? 'bg-primary-500' : 'bg-secondary-500'} animate-pulse`}></div>
              <span className="text-gray-700 font-medium">
                Current turn: {gameStatus?.currentTurn}
              </span>
            </div>
          )}
          { [GameStatus.O_WON, GameStatus.X_WON, GameStatus.DRAW].includes(gameStatus?.status) && (
            <span className="text-gray-700 font-medium">Game Over</span>
          )}
        </div>
      </div>
    </div>
  );
}