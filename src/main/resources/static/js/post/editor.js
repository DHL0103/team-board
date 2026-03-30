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

    // ── 이미지 카운터 ──
    const imgCounter = toolbar.querySelector('.editor-img-counter');
    function updateImageCounter() {
        let count = 0;
        editor.state.doc.descendants(node => { if (node.type.name === 'image') { count++; } });
        if (count > 0) {
            imgCounter.textContent = count + ' / 5';
            imgCounter.style.display = '';
        } else {
            imgCounter.style.display = 'none';
        }
    }

    // ── 이미지 업로드 ──
    const imgInput = toolbar.querySelector('.editor-img-input');
    toolbar.querySelector('[data-cmd="image"]').addEventListener('click', () => imgInput.click());
    const MAX_INLINE_IMAGES = 5;
    imgInput.addEventListener('change', () => {
        const file = imgInput.files[0];
        if (!file) { return; }

        let imageCount = 0;
        editor.state.doc.descendants(node => { if (node.type.name === 'image') { imageCount++; } });
        if (imageCount >= MAX_INLINE_IMAGES) {
            App.showToast('이미지는 최대 ' + MAX_INLINE_IMAGES + '개까지 삽입할 수 있습니다.');
            imgInput.value = '';
            return;
        }

        const formData = new FormData();
        formData.append('file', file);
        const csrfToken  = document.querySelector('meta[name="_csrf"]').content;
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;
        fetch('/post/image', { method: 'POST', body: formData, headers: { [csrfHeader]: csrfToken } })
            .then(res => res.json())
            .then(data => {
                editor.chain().focus().setImage({ src: data.url }).run();
                const endPos = editor.state.doc.content.size;
                editor.chain().insertContentAt(endPos, { type: 'paragraph' }).run();
            });
        imgInput.value = '';
    });

    // ── 툴바 active 상태 갱신 ──
    editor.on('selectionUpdate', () => updateToolbar(editor, toolbar));
    editor.on('transaction',     () => { updateToolbar(editor, toolbar); updateImageCounter(); });

    // ── 글자 수 카운터 ──
    const charCountEl = document.getElementById('post-editor-char-count');
    const MAX_CHARS = 10000;
    function updateCharCount() {
        if (!charCountEl) { return; }
        const len = editor.getText().replace(/\n$/, '').length;
        charCountEl.textContent = len.toLocaleString();
        document.getElementById('post-editor-char-counter').classList.toggle('near-limit', len >= MAX_CHARS * 0.9);
    }
    editor.on('update', updateCharCount);
    updateCharCount();

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
