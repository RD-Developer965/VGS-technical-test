import { useNavigate } from 'react-router-dom';
import { GameStatus } from '../types/game';

interface GameOverModalProps {
  isOpen: boolean;
  status: GameStatus
}

export default function GameOverModal({ isOpen, status }: GameOverModalProps) {
  const navigate = useNavigate();

  const winner = status === GameStatus.X_WON ? 'X' : status === GameStatus.O_WON ? 'O' : 'DRAW';

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
      <div className="bg-white p-8 rounded-xl shadow-2xl max-w-sm w-full mx-4 transform transition-all">
        <h2 className="text-2xl font-bold text-gray-800 mb-4 text-center">
          {status === GameStatus.DRAW ? "It's a Draw!" : `Player ${winner} Wins! 🎉`}
        </h2>
        <p className="text-gray-600 mb-6 text-center">
          {winner === 'DRAW' 
            ? "Great game! Both players played excellently."
            : "Congratulations on your victory!"}
        </p>
        <div className="flex justify-center">
          <button
            onClick={() => navigate('/')}
            className="appearance-none border-0 bg-gray-900 text-white px-6 py-2 rounded-lg font-semibold 
                     transform transition-all duration-200 hover:scale-105 hover:bg-gray-800
                     focus:outline-none focus:ring-2 focus:ring-gray-700 focus:ring-opacity-50"
          >
            Play Again
          </button>
        </div>
      </div>
    </div>
  );
}