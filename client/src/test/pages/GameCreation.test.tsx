import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { vi } from 'vitest';
import axios from 'axios';
import MockAdapter from 'axios-mock-adapter';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { toast } from 'sonner';
import { useGameStore } from '../../store/gameStore';
import GameCreation from '../../pages/GameCreation';

// -------------------
// Mocks
// -------------------

const toastErrorMock = vi.fn();

vi.mock('sonner', () => ({
  toast: { error: toastErrorMock },
}));

const mockAxios = new MockAdapter(axios);
const mockNavigate = vi.fn();

vi.mock('react-router-dom', () => ({
  useNavigate: () => mockNavigate,
}));

vi.mock('sonner', () => ({
  toast: {
    error: vi.fn(),
  },
}));

// -------------------
// Helper para renderizar con React Query
// -------------------
const renderWithClient = (ui: React.ReactElement) => {
  const queryClient = new QueryClient({ defaultOptions: { queries: { retry: false } } });
  return render(<QueryClientProvider client={queryClient}>{ui}</QueryClientProvider>);
};

// -------------------
// Test Suite
// -------------------
describe('GameCreation', () => {
  const setCurrentGameIdMock = vi.fn();

  beforeEach(() => {
    useGameStore.setState({
      currentGameId: null,
      setCurrentGameId: setCurrentGameIdMock,
      currentPlayer: 'X',
      setCurrentPlayer: vi.fn(),
    });

    mockAxios.reset();
    mockNavigate.mockReset();
    toastErrorMock.mockReset();
    setCurrentGameIdMock.mockReset();
  });

  it('renders the component', () => {
    renderWithClient(<GameCreation />);
    expect(screen.getByText(/Welcome to Tic Tac Toe/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /New Game/i })).toBeInTheDocument();
  });

  it('creates a new game successfully and navigates', async () => {
    const fakeGame = { id: 123 };
    mockAxios.onPost(/games\/create/).reply(200, fakeGame);

    renderWithClient(<GameCreation />);

    const newGameButton = screen.getByRole('button', { name: /New Game/i });
    fireEvent.click(newGameButton);

    await waitFor(() => {
      expect(setCurrentGameIdMock).toHaveBeenCalledWith(fakeGame.id);
      expect(mockNavigate).toHaveBeenCalledWith(`/game/${fakeGame.id}`);
    });
  });

  it('shows toast error when creation fails', async () => {
    mockAxios.onPost(/games\/create/).reply(400, { message: 'Creation failed' });

    renderWithClient(<GameCreation />);

    const newGameButton = screen.getByRole('button', { name: /New Game/i });
    fireEvent.click(newGameButton);

    await waitFor(() => {
      expect(toast.error).toHaveBeenCalledWith('Creation failed');
    });
  });
});
