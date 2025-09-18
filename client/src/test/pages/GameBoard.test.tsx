import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { vi } from 'vitest';
import axios from 'axios';
import MockAdapter from 'axios-mock-adapter';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { toast } from 'sonner';
import GameBoard from '../../pages/GameBoard';
import { useGameStore } from '../../store/gameStore';
import { CellValue, GameStatus } from '../../types/game';

// -------------------
// Mocks
// -------------------
const mockAxios = new MockAdapter(axios);
vi.mock('react-router-dom', () => ({
  useParams: () => ({ gameId: '1' }),
  useNavigate: () => vi.fn(), // Mock navigate
}));


vi.mock('sonner', () => ({
  toast: {
    error: vi.fn(),
  },
}));


const renderWithClient = (ui: React.ReactElement) => {
  const queryClient = new QueryClient({
    defaultOptions: { queries: { retry: false } },
  });

  return render(<QueryClientProvider client={queryClient}>{ui}</QueryClientProvider>);
};

// -------------------
// Test Suite
// -------------------
describe('GameBoard', () => {
  beforeEach(() => {
    // Reset Zustand store
    useGameStore.setState({
      currentPlayer: 'X',
      setCurrentPlayer: vi.fn(),
      currentGameId: 1,
      setCurrentGameId: vi.fn(),
    });

    mockAxios.reset();
  });

  it('renders loading state initially', () => {
    renderWithClient(<GameBoard />);
    expect(screen.getByText(/Loading game/i)).toBeInTheDocument();
  });

  it('renders board when data is loaded', async () => {
    const fakeGame = {
      id: 1,
      board: [
        { row: 1, column: 1, value: CellValue.EMPTY },
        { row: 1, column: 2, value: CellValue.X },
        { row: 1, column: 3, value: CellValue.O },
      ],
      currentTurn: 'X',
      status: GameStatus.IN_PROGRESS,
    };

    mockAxios.onGet(/games\/status/).reply(200, fakeGame);
    renderWithClient(<GameBoard />);

    // Wait for board to be rendered
    await waitFor(() => {
      expect(screen.getByText('X')).toBeInTheDocument();
      expect(screen.getByText('O')).toBeInTheDocument();
    });

    // Click on an empty cell
    const emptyCell = screen.getAllByRole('button').find(
        (btn) => btn.textContent === '' && !btn.hasAttribute('disabled')
    );
    fireEvent.click(emptyCell!);
  });

  it('shows toast error on failed move', async () => {
    const fakeGame = {
      id: 1,
      board: [{ row: 1, column: 1, value: CellValue.EMPTY }],
      currentTurn: 'X',
      status: GameStatus.IN_PROGRESS,
    };

    mockAxios.onGet(/games\/status/).reply(200, fakeGame);
    mockAxios.onPost(/games\/move/).reply(400, { message: 'Invalid move' });

    renderWithClient(<GameBoard />);

    await waitFor(() => expect(screen.getByText(/Current turn/i)).toBeInTheDocument());

    const emptyCell = screen.getAllByRole('button').find(
    (btn) => btn.textContent === '' && !btn.hasAttribute('disabled')
    );
    fireEvent.click(emptyCell!);

    await waitFor(() => {
      expect(toast.error).toHaveBeenCalledWith('Invalid move');
    });
  });
});
