import { Editor } from 'https://esm.sh/@tiptap/core@2';
import StarterKit from 'https://esm.sh/@tiptap/starter-kit@2';
import Image from 'https://esm.sh/@tiptap/extension-image@2';

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
        ],
        content: initialContent || '',
    });

    // ── 툴바 버튼 ──
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
                editor.chain().focus().setImage({ src: data.url }).run();
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
    const states = ['bold', 'italic', 'strike', 'bulletList', 'orderedList'];
    states.forEach(name => {
        const btn = toolbar.querySelector(`[data-cmd="${name}"]`);
        if (btn) {
            btn.classList.toggle('active', editor.isActive(name));
        }
    });
}

// ── 초기화 ──
const createEditorEl = document.getElementById('create-editor');
if (createEditorEl) {
    initEditor('create-editor', 'create-content', 'create-toolbar', '');
}

const editEditorEl = document.getElementById('edit-editor');
if (editEditorEl) {
    const initialContent = editEditorEl.dataset.content || '';
    initEditor('edit-editor', 'edit-content', 'edit-toolbar', initialContent);
}
