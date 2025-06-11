export interface Report {
    reportId: number;
    userName: string;
    examId: number;
    correctAnswers: number;
    totalQuestions: number;
    score: number;
    percentage: number;
    feedback: string | null;
  }