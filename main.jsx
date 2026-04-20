import React, { useState, useRef, useEffect } from 'react';
import { createRoot } from 'react-dom/client';
import { Upload, Brain, Zap, Target, Loader2, Send, Shield, Image as ImageIcon, BarChart3 } from 'lucide-react';

const getApiKey = () => import.meta.env.VITE_GEMINI_API_KEY || "";
const API_KEY = getApiKey();
const PHI = 1.61803398875;

const App = () => {
  const [image, setImage] = useState(null);
  const [base64, setBase64] = useState(null);
  const [loading, setLoading] = useState(false);
  const [results, setResults] = useState(null);
  const [marketInsights, setMarketInsights] = useState(null);
  const [chatInput, setChatInput] = useState("");
  const [chatHistory, setChatHistory] = useState([{ role: 'assistant', content: "PSSF Neural Interface ready. Upload a marketing stimulus to begin." }]);
  const [isTyping, setIsTyping] = useState(false);
  
  const canvasRef = useRef(null);
  const fileInputRef = useRef(null);
  const chatEndRef = useRef(null);

  useEffect(() => {
    chatEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [chatHistory, isTyping]);

  const handleFileUpload = (e) => {
    const file = e.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = (ev) => {
        setImage(ev.target.result);
        setBase64(ev.target.result.split(',')[1]);
        setResults(null);
        setMarketInsights(null);
      };
      reader.readAsDataURL(file);
    }
  };

  const runAnalysis = async () => {
    if (!base64 || !API_KEY) {
      setChatHistory(prev => [...prev, { role: 'assistant', content: !API_KEY ? "❌ VITE_GEMINI_API_KEY not set in .env" : "Please upload an image first." }]);
      return;
    }
    setLoading(true);
    try {
      const prompt = `Task: Detect focal points for marketing analysis. Output strictly JSON: {"analysis":[{"label":"string","box_2d":[ymin,xmin,ymax,xmax],"rarity":0.1}]}`;
      
      const res = await fetch(`https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=${API_KEY}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          contents: [{ parts: [{ text: prompt }, { inlineData: { mimeType: "image/jpeg", data: base64 } }] }],
          generationConfig: { responseMimeType: "application/json" }
        })
      });

      const data = await res.json();
      const raw = JSON.parse(data.candidates[0].content.parts[0].text);

      const processed = (raw.analysis || []).map(obj => ({
        ...obj,
        focusScore: (PHI * -Math.log2(obj.rarity || 0.5)) * 1.2
      })).sort((a, b) => b.focusScore - a.focusScore);

      setResults(processed);
      setChatHistory(prev => [...prev, { role: 'assistant', content: `✅ Analysis complete. Primary focal anchor: **${processed[0]?.label || "N/A"}**` }]);
    } catch (err) {
      setChatHistory(prev => [...prev, { role: 'assistant', content: "⚠️ Neural link failed. Check API key and image." }]);
    } finally {
      setLoading(false);
    }
  };

  // ==================== NEW: MARKET ENHANCEMENT ENGINE ====================
  const enhanceMarket = async () => {
    if (!results || !base64) return;
    setLoading(true);
    try {
      const prompt = `You are a world-class marketing strategist. Using the PSSF analysis below, generate a complete market enhancement report:

PSSF Results: ${JSON.stringify(results.slice(0, 5))}

Image context: marketing stimulus.

Return ONLY this JSON:
{
  "targetAudience": "string",
  "positioning": "string",
  "adCopy": "string",
  "predictedEngagement": "number (0-100)",
  "recommendations": ["string", "string", "string"]
}`;

      const res = await fetch(`https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=${API_KEY}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          contents: [{ parts: [{ text: prompt }, { inlineData: { mimeType: "image/jpeg", data: base64 } }] }]
        })
      });

      const data = await res.json();
      const insights = JSON.parse(data.candidates[0].content.parts[0].text);
      setMarketInsights(insights);
    } catch (e) {
      alert("Market enhancement failed. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  const handleChat = async (e) => {
    e.preventDefault();
    if (!chatInput.trim()) return;
    const msg = chatInput;
    setChatInput("");
    setChatHistory(prev => [...prev, { role: 'user', content: msg }]);
    setIsTyping(true);

    try {
      const res = await fetch(`https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=${API_KEY}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ contents: [{ parts: [{ text: msg }] }] })
      });
      const data = await res.json();
      setChatHistory(prev => [...prev, { role: 'assistant', content: data.candidates[0].content.parts[0].text }]);
    } catch {
      setChatHistory(prev => [...prev, { role: 'assistant', content: "Neural link timeout." }]);
    } finally {
      setIsTyping(false);
    }
  };

  useEffect(() => {
    if (results && canvasRef.current && image) {
      const c = canvasRef.current;
      const ctx = c.getContext('2d');
      const img = new Image();
      img.onload = () => {
        c.width = img.width; c.height = img.height;
        ctx.drawImage(img, 0, 0);
        results.forEach((o, i) => {
          ctx.strokeStyle = i === 0 ? '#f59e0b' : '#6366f1';
          ctx.lineWidth = Math.max(6, img.width / 70);
          const x = (o.box_2d[1] / 1000) * c.width;
          const y = (o.box_2d[0] / 1000) * c.height;
          const w = ((o.box_2d[3] - o.box_2d[1]) / 1000) * c.width;
          const h = ((o.box_2d[2] - o.box_2d[0]) / 1000) * c.height;
          ctx.strokeRect(x, y, w, h);
          
          ctx.fillStyle = i === 0 ? '#f59e0b' : '#6366f1';
          ctx.font = 'bold 16px Inter';
          ctx.fillText(o.label, x + 8, y + 22);
        });
      };
      img.src = image;
    }
  }, [results, image]);

  return (
    <div className="min-h-screen bg-[#020306] text-slate-100 p-4 md:p-8 font-sans">
      <div className="max-w-7xl mx-auto space-y-8">
        {/* HEADER */}
        <header className="flex flex-col md:flex-row justify-between items-center bg-[#0a0d14] border border-white/10 p-6 rounded-3xl">
          <div className="flex items-center gap-4">
            <Brain className="text-amber-500 w-9 h-9" />
            <div>
              <h1 className="text-3xl font-black tracking-tighter">PSSF.MARKET</h1>
              <p className="text-xs uppercase tracking-[0.125em] text-slate-500">Dr. Fayaz Ahmed Siyal</p>
            </div>
          </div>

          <div className="flex gap-3">
            <button onClick={() => fileInputRef.current?.click()} 
                    className="bg-white/5 hover:bg-white/10 px-6 py-3 rounded-2xl text-sm font-bold border border-white/10 flex items-center gap-2">
              <Upload size={16} /> Upload Stimulus
            </button>
            {image && (
              <button onClick={runAnalysis} 
                      className="bg-amber-500 text-black px-6 py-3 rounded-2xl text-sm font-bold flex items-center gap-2">
                <Zap size={16} /> Analyze
              </button>
            )}
            {results && (
              <button onClick={enhanceMarket} 
                      className="bg-gradient-to-r from-indigo-500 to-amber-500 text-white px-6 py-3 rounded-2xl text-sm font-bold flex items-center gap-2">
                <BarChart3 size={16} /> Market Audit ✨
              </button>
            )}
          </div>
          <input type="file" ref={fileInputRef} className="hidden" onChange={handleFileUpload} accept="image/*" />
        </header>

        <div className="grid grid-cols-1 lg:grid-cols-12 gap-8">
          {/* IMAGE + CANVAS */}
          <div className="lg:col-span-8 space-y-6">
            <div className="bg-[#0a0d14] border border-white/10 rounded-3xl min-h-[520px] flex items-center justify-center relative overflow-hidden">
              {!image ? (
                <div className="text-center opacity-30">
                  <ImageIcon size={72} className="mx-auto mb-4" />
                  <p className="uppercase text-xs font-black tracking-widest">Awaiting Marketing Stimulus</p>
                </div>
              ) : (
                <canvas ref={canvasRef} className="max-w-full max-h-[520px] rounded-2xl shadow-2xl" />
              )}
              {loading && (
                <div className="absolute inset-0 bg-black/80 backdrop-blur-xl flex flex-col items-center justify-center">
                  <Loader2 className="animate-spin text-amber-500 w-12 h-12 mb-4" />
                  <p className="text-xs font-black uppercase tracking-widest">PSSF Neural Analysis in progress...</p>
                </div>
              )}
            </div>

            {/* CHAT */}
            <div className="bg-[#0a0d14] border border-white/10 rounded-3xl h-[420px] flex flex-col overflow-hidden">
              <div className="flex-1 overflow-y-auto p-6 space-y-6">
                {chatHistory.map((m, i) => (
                  <div key={i} className={`flex ${m.role === 'user' ? 'justify-end' : 'justify-start'}`}>
                    <div className={`max-w-[80%] px-5 py-4 rounded-3xl text-sm leading-relaxed ${m.role === 'user' ? 'bg-indigo-600' : 'bg-white/5 border border-white/10'}`}>
                      {m.content}
                    </div>
                  </div>
                ))}
                {isTyping && <div className="text-amber-400 text-sm animate-pulse pl-4">Thinking...</div>}
                <div ref={chatEndRef} />
              </div>
              <form onSubmit={handleChat} className="p-4 border-t border-white/10 flex gap-3">
                <input
                  value={chatInput}
                  onChange={e => setChatInput(e.target.value)}
                  placeholder="Ask about visual strategy, attention, or marketing..."
                  className="flex-1 bg-white/5 border border-white/10 rounded-2xl px-5 py-4 text-sm focus:outline-none focus:border-amber-400"
                />
                <button type="submit" className="bg-amber-500 text-black p-4 rounded-2xl hover:bg-amber-400">
                  <Send size={20} />
                </button>
              </form>
            </div>
          </div>

          {/* SIDEBAR */}
          <div className="lg:col-span-4 space-y-6">
            {/* Neural Metrics */}
            <div className="bg-[#0a0d14] border border-white/10 p-6 rounded-3xl">
              <h2 className="text-xs font-black uppercase tracking-widest text-slate-400 mb-5 flex items-center gap-2">
                <Target size={14} className="text-amber-500" /> ATTENTION HIERARCHY
              </h2>
              {results ? results.slice(0, 6).map((r, i) => (
                <div key={i} className="mb-6 last:mb-0">
                  <div className="flex justify-between text-xs font-bold mb-1.5">
                    <span className={i === 0 ? "text-amber-400" : ""}>{r.label}</span>
                    <span className="text-slate-500">{r.focusScore.toFixed(1)}</span>
                  </div>
                  <div className="h-2 bg-white/10 rounded-full overflow-hidden">
                    <div className={`h-full ${i === 0 ? 'bg-amber-500' : 'bg-indigo-500'}`} 
                         style={{ width: `${Math.min(100, r.focusScore * 6.8)}%` }} />
                  </div>
                </div>
              )) : <div className="py-16 text-center opacity-20 text-xs font-black uppercase">Upload &amp; analyze to see PSSF scores</div>}
            </div>

            {/* MARKET INSIGHTS */}
            {marketInsights && (
              <div className="bg-gradient-to-br from-indigo-500/10 to-amber-500/10 border border-white/10 p-6 rounded-3xl">
                <h2 className="text-xs font-black uppercase tracking-widest text-amber-400 mb-4">✨ Market Enhancement Report</h2>
                <div className="space-y-5 text-sm">
                  <div>
                    <div className="text-xs text-slate-400">TARGET AUDIENCE</div>
                    <p className="font-medium">{marketInsights.targetAudience}</p>
                  </div>
                  <div>
                    <div className="text-xs text-slate-400">POSITIONING</div>
                    <p className="font-medium">{marketInsights.positioning}</p>
                  </div>
                  <div>
                    <div className="text-xs text-slate-400">AD COPY</div>
                    <p className="italic border-l-2 border-amber-400 pl-3">{marketInsights.adCopy}</p>
                  </div>
                  <div className="flex items-center justify-between">
                    <div className="text-xs text-slate-400">PREDICTED ENGAGEMENT</div>
                    <div className="text-3xl font-black text-amber-400">{marketInsights.predictedEngagement}%</div>
                  </div>
                </div>
              </div>
            )}

            {/* LICENSE */}
            <div className="text-[10px] text-slate-500 border border-white/10 bg-white/5 p-5 rounded-3xl leading-relaxed">
              © <span className="font-bold text-slate-300">Dr. Fayaz Ahmed Siyal</span><br />
              All rights reserved.<br />
              <span className="italic">Purpose: Enhancing marketing of products and for general use for people.</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

// Render
const container = document.getElementById('root');
if (container) {
  if (!container._root) container._root = createRoot(container);
  container._root.render(<App />);
}