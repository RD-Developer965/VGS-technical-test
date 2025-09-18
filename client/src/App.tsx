import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import TicTacToe from './components/TicTacToe';
import './App.css';

const queryClient = new QueryClient();

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <div className="min-h-screen bg-gradient-to-br from-primary-50 via-white to-secondary-50 flex items-center justify-center p-4">
        <div className="w-full max-w-4xl mx-auto">
          <TicTacToe />
        </div>
      </div>
    </QueryClientProvider>
  );
}

export default App;
