import { Editor } from 'https://esm.sh/@tiptap/core@2';
import StarterKit from 'https://esm.sh/@tiptap/starter-kit@2';
import Image from 'https://esm.sh/@tiptap/extension-image@2';
import Underline from 'https://esm.sh/@tiptap/extension-underline@2';
import Link from 'https://esm.sh/@tiptap/extension-link@2';

function initEditor(editorId, inputId, toolbarId, initialContent) {
    const editorEl = document.getElementById(editorId);
    const inputEl  = document.getElementById(inputId);
    const toolbar  = document.getElementById(toolbarId);
    if (!editorEl || !inputEl || !toolbar) { return; }

    const editor = new Editor({
        element: editorEl,
        extensions: [
            StarterKit,
            Image.configure({ inline: false, allowBase64: false }),
            Underline,
            Link.configure({
                openOnClick: false,
                HTMLAttributes: { target: '_blank', rel: 'noopener noreferrer' },
            }),
        ],
        content: initialContent || '',
    });

    // ── 툴바 버튼 ──
    toolbar.querySelector('[data-cmd="h1"]')
        .addEventListener('click', () => editor.chain().focus().toggleHeading({ level: 1 }).run());
    toolbar.querySelector('[data-cmd="h2"]')
        .addEventListener('click', () => editor.chain().focus().toggleHeading({ level: 2 }).run());
    toolbar.querySelector('[data-cmd="h3"]')
        .addEventListener('click', () => editor.chain().focus().toggleHeading({ level: 3 }).run());
    toolbar.querySelector('[data-cmd="link"]').addEventListener('click', () => {
        if (editor.isActive('link')) {
            editor.chain().focus().unsetLink().run();
            return;
        }
        const { from, to } = editor.state.selection;
        const selectedText = editor.state.doc.textBetween(from, to).trim();
        if (selectedText) {
            editor.chain().focus().setLink({ href: selectedText }).run();
        }
    });
    toolbar.querySelector('[data-cmd="underline"]')
        .addEventListener('click', () => editor.chain().focus().toggleUnderline().run());
    toolbar.querySelector('[data-cmd="bold"]')
        .addEventListener('click', () => editor.chain().focus().toggleBold().run());
    toolbar.querySelector('[data-cmd="italic"]')
        .addEventListener('click', () => editor.chain().focus().toggleItalic().run());
    toolbar.querySelector('[data-cmd="strike"]')
        .addEventListener('click', () => editor.chain().focus().toggleStrike().run());
    toolbar.querySelector('[data-cmd="bulletList"]')
        .addEventListener('click', () => editor.chain().focus().toggleBulletList().run());
    toolbar.querySelector('[data-cmd="orderedList"]')
        .addEventListener('click', () => editor.chain().focus().toggleOrderedList().run());
    toolbar.querySelector('[data-cmd="codeBlock"]')
        .addEventListener('click', () => editor.chain().focus().toggleCodeBlock().run());

    // ── 이미지 업로드 ──
    const imgInput = toolbar.querySelector('.editor-img-input');
    toolbar.querySelector('[data-cmd="image"]').addEventListener('click', () => imgInput.click());
    imgInput.addEventListener('change', () => {
        const file = imgInput.files[0];
        if (!file) { return; }
        const formData = new FormData();
        formData.append('file', file);
        fetch('/post/image', { method: 'POST', body: formData })
            .then(res => res.json())
            .then(data => {
                editor.chain().focus().setImage({ src: data.url }).insertContentAt(editor.state.doc.content.size, { type: 'paragraph' }).run();
            });
        imgInput.value = '';
    });

    // ── 툴바 active 상태 갱신 ──
    editor.on('selectionUpdate', () => updateToolbar(editor, toolbar));
    editor.on('transaction',     () => updateToolbar(editor, toolbar));

    // ── form submit 시 HTML 복사 ──
    editorEl.closest('form').addEventListener('submit', () => {
        inputEl.value = editor.getHTML();
    });
}

function updateToolbar(editor, toolbar) {
    const states = ['link', 'underline', 'bold', 'italic', 'strike', 'bulletList', 'orderedList', 'codeBlock'];
    states.forEach(name => {
        const btn = toolbar.querySelector(`[data-cmd="${name}"]`);
        if (btn) {
            btn.classList.toggle('active', editor.isActive(name));
        }
    });
    [1, 2, 3].forEach(level => {
        const btn = toolbar.querySelector(`[data-cmd="h${level}"]`);
        if (btn) {
            btn.classList.toggle('active', editor.isActive('heading', { level }));
        }
    });
}

// ── 초기화 ──
const postEditorEl = document.getElementById('post-editor');
if (postEditorEl) {
    const initialContent = postEditorEl.dataset.content || '';
    initEditor('post-editor', 'post-content', 'post-toolbar', initialContent);
}
