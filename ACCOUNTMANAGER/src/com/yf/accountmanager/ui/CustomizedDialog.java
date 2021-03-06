package com.yf.accountmanager.ui;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.yf.accountmanager.R;
import com.yf.accountmanager.adapter.OptionListAdapter;
import com.yf.accountmanager.common.FileManager;
import com.yf.accountmanager.common.FileManager.ConditionalSearchFileFilter;
import com.yf.accountmanager.util.CommonUtils;
import com.yf.accountmanager.util.FileUtils;
import com.yf.accountmanager.util.SharePrefUtil;
import com.yf.accountmanager.util.StringUtil;
import com.yf.accountmanager.util.FileUtils.DirSizeCalculator;
import com.yf.filesystem.FileSearcherAdapter;
import com.yf.filesystem.FileSystemAdapter;

public class CustomizedDialog extends Dialog {
	public TextView title, message, dropDown, label1, label2;

	public Button positiveButton, negativeButton, button1, button2, button3,
			button4;

	public CheckedTextView checkedText1;

	public GridView gv, gv2;

	public ListView lv;

	public EditText editText;
	
	public ViewGroup vg1;

	public ImageButton back, add, sort, screen, clipBoard, refresh, remove,
			zip, imgButton1, imgButton2;

	public ProgressBar pb1, pb2;
	
	public DirSizeCalculator dirSizeCalculator;

	public static final int resId = R.layout.dialog_customized,
			chooserResId = R.layout.dialog_chooselist,
			optionsResId = R.layout.dialog_optionlist,
			editorResId = R.layout.dialog_edittext,
			fileChooserResId = R.layout.dialog_filechooser,
			saveasResId = R.layout.dialog_saveas,
			screenDialogResId = R.layout.dialog_screening,
			clipBoardResId = R.layout.dialog_clipboard,
			shareResId = R.layout.dialog_sharelist,
			multiButtonResId = R.layout.dialog_multibutton,
			progressResId = R.layout.dialog_progress,
			searcherResId = R.layout.dialog_filesearcher,
			searchAdvancedResId = R.layout.dialog_search_advanced,
			fileSystemSettingsDialogResId = R.layout.dialog_filesystem_settings;

	public CustomizedDialog(Context context) {
		super(context);
	}

	CustomizedDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public CustomizedDialog(Context context, int theme) {
		super(context, theme);
	}

	public static void setDialogsWindowSoftInputModeToHideAlways(Dialog dialog) {
		dialog.getWindow().getAttributes().softInputMode |= WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN;
	}

	public boolean getCheckedTextStatus() {
		return checkedText1.isChecked();
	}

	// positiveButton
	public CustomizedDialog setPositiveButton(String name,
			final DialogInterface.OnClickListener listener) {
		positiveButton.setVisibility(View.VISIBLE);
		positiveButton.setText(name);
		positiveButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dismiss();
				if (listener != null)
					listener.onClick(CustomizedDialog.this,
							DialogInterface.BUTTON_POSITIVE);
			}
		});
		return this;
	}

	// NegativeButton
	public CustomizedDialog setNegativeButton(String name,
			final DialogInterface.OnClickListener listener) {
		negativeButton.setVisibility(View.VISIBLE);
		negativeButton.setText(name);
		negativeButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dismiss();
				if (listener != null)
					listener.onClick(CustomizedDialog.this,
							DialogInterface.BUTTON_NEGATIVE);
			}
		});
		return this;
	}

	// DefaultDialog
	public static CustomizedDialog initDialog(String title, String message,
			String checkedText, float fontSize, Context context) {
		View contentView = LayoutInflater.from(context).inflate(resId, null,
				false);
		CustomizedDialog dialog = initDialog(contentView, true, false, context,
				-1);
		dialog.title = ((TextView) contentView.findViewById(R.id.title));
		dialog.message = ((TextView) contentView.findViewById(R.id.message));
		dialog.positiveButton = ((Button) contentView
				.findViewById(R.id.positiveButton));
		dialog.negativeButton = (Button) contentView
				.findViewById(R.id.negativeButton);
		dialog.checkedText1 = (CheckedTextView) contentView
				.findViewById(R.id.checkedTextView1);
		dialog.title.setText(title);
		if (fontSize != 0)
			dialog.message.setTextSize(fontSize);
		dialog.message.setText(message);
		if (checkedText != null) {
			dialog.checkedText1.setText(checkedText);
			CommonUtils
					.setOnClickListenerForCheckedTextView(dialog.checkedText1);
		} else {
			dialog.checkedText1.setVisibility(View.GONE);
		}
		dialog.positiveButton.setVisibility(View.GONE);
		dialog.negativeButton.setVisibility(View.GONE);
		return dialog;
	}

	// chooseListDailog
	public static CustomizedDialog initChooseDialog(String title,
			final Context context) {
		View contentView = LayoutInflater.from(context).inflate(chooserResId,
				null, false);
		CustomizedDialog dialog = initDialog(contentView, true, true, context,
				-1);
		dialog.title = ((TextView) contentView.findViewById(R.id.title));
		dialog.gv = (GridView) contentView.findViewById(R.id.gridView1);
		dialog.title.setText(title);
		dialog.remove = (ImageButton) contentView.findViewById(R.id.remove);
		dialog.dropDown = (TextView) contentView.findViewById(R.id.dropdown);
		dialog.remove.setVisibility(View.GONE);
		dialog.dropDown.setVisibility(View.GONE);
		Drawable drawable = context.getResources().getDrawable(
				R.drawable.favorite);
		int size = context.getResources().getDimensionPixelSize(R.dimen.dp_35);
		drawable.setBounds(new Rect(0, 0, size, size));
		dialog.title.setCompoundDrawables(drawable, null, null, null);
		return dialog;
	}

	// shareListDailog
	public static CustomizedDialog initShareDialog(String title,
			final Context context) {
		View contentView = LayoutInflater.from(context).inflate(shareResId,
				null, false);
		final CustomizedDialog dialog = initDialog(contentView, true, true,
				context, -1);
		dialog.title = ((TextView) contentView.findViewById(R.id.title));
		dialog.gv = (GridView) contentView.findViewById(R.id.gridView1);
		dialog.title.setText(title);
		dialog.title.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		return dialog;
	}

	// OptionDialog
	public static CustomizedDialog initOptionDialog(Context context) {
		View contentView = LayoutInflater.from(context).inflate(optionsResId,
				null, false);
		final CustomizedDialog dialog = initDialog(contentView, true, true,
				context, -1);
		dialog.title = ((TextView) contentView.findViewById(R.id.title));
		dialog.label1= ((TextView) contentView.findViewById(R.id.textView1));
		dialog.label2= ((TextView) contentView.findViewById(R.id.textView2));
		dialog.lv = (ListView) contentView.findViewById(R.id.listView1);
		dialog.dropDown = (TextView) contentView.findViewById(R.id.dropdown);
		dialog.dropDown.setVisibility(View.GONE);
		dialog.vg1=(ViewGroup)contentView.findViewById(R.id.relativeLayout1);
		dialog.vg1.setVisibility(View.GONE);
		dialog.title.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		return dialog;
	}

	// EditDialog
	public static CustomizedDialog initEditDialog(Context context) {
		View contentView = LayoutInflater.from(context).inflate(editorResId,
				null, false);
		CustomizedDialog dialog = initDialog(contentView, true, false, context,
				-1);
		dialog.editText = (EditText) contentView.findViewById(R.id.editText1);
		dialog.button1 = (Button) contentView.findViewById(R.id.button1);
		dialog.positiveButton = (Button) contentView
				.findViewById(R.id.positiveButton);
		dialog.negativeButton = (Button) contentView
				.findViewById(R.id.negativeButton);
		CommonUtils.bindEditTextNtextDisposer(dialog.editText, dialog.button1);
		return dialog;
	}

	// FileChooser
	public static CustomizedDialog initFileChooser(final Context context) {
		View contentView = LayoutInflater.from(context).inflate(
				fileChooserResId, null, false);
		CustomizedDialog dialog = initDialog(contentView, true, false, context,
				context.getResources().getDimension(R.dimen.dp_350));

		dialog.title = ((TextView) contentView.findViewById(R.id.title));
		dialog.back = (ImageButton) contentView.findViewById(R.id.back);
		dialog.gv = (GridView) contentView.findViewById(R.id.gridView1);
		dialog.positiveButton = (Button) contentView
				.findViewById(R.id.positiveButton);
		dialog.negativeButton = (Button) contentView
				.findViewById(R.id.negativeButton);
		dialog.add = (ImageButton) contentView.findViewById(R.id.add);
		dialog.sort = (ImageButton) contentView.findViewById(R.id.sort);
		dialog.screen = (ImageButton) contentView.findViewById(R.id.screen);
		dialog.clipBoard = (ImageButton) contentView
				.findViewById(R.id.clipBoard);
		dialog.refresh = (ImageButton) contentView.findViewById(R.id.refresh);
		return dialog;
	}

	// FileChooser
	public static CustomizedDialog initSaveasDialog(final Context context) {
		View contentView = LayoutInflater.from(context).inflate(saveasResId,
				null, false);
		CustomizedDialog dialog = initDialog(contentView, true, false, context,
				context.getResources().getDimension(R.dimen.dp_350));
		dialog.title = ((TextView) contentView.findViewById(R.id.title));
		dialog.back = (ImageButton) contentView.findViewById(R.id.back);
		dialog.gv = (GridView) contentView.findViewById(R.id.gridView1);
		dialog.positiveButton = (Button) contentView
				.findViewById(R.id.positiveButton);
		dialog.negativeButton = (Button) contentView
				.findViewById(R.id.negativeButton);
		dialog.add = (ImageButton) contentView.findViewById(R.id.add);
		dialog.dropDown = (TextView) contentView.findViewById(R.id.dropdown);
		dialog.editText = (EditText) contentView.findViewById(R.id.editText1);
		dialog.button1 = (Button) contentView.findViewById(R.id.button1);
		CommonUtils.bindEditTextNtextDisposer(dialog.editText, dialog.button1);
		dialog.editText.setText("untitled");
		dialog.editText.setSelection(dialog.editText.length());
		setDialogsWindowSoftInputModeToHideAlways(dialog);
		return dialog;
	}

	// clipBoardDialog
	public static CustomizedDialog initClipBoardDialog(Context context) {
		View contentView = LayoutInflater.from(context).inflate(clipBoardResId,
				null, false);
		CustomizedDialog dialog = initDialog(contentView, true, false, context,
				context.getResources().getDimension(R.dimen.dp_350));
		dialog.gv = (GridView) contentView.findViewById(R.id.gridView1);
		dialog.gv2 = (GridView) contentView.findViewById(R.id.gridView2);
		dialog.back = (ImageButton) contentView.findViewById(R.id.back);
		dialog.positiveButton = (Button) contentView
				.findViewById(R.id.positiveButton);
		dialog.negativeButton = (Button) contentView
				.findViewById(R.id.negativeButton);
		dialog.zip = (ImageButton) contentView.findViewById(R.id.zip);
		return dialog;
	}

	// ScreenDialog
	public static CustomizedDialog initScreenDialog(Context context) {
		View contentView = LayoutInflater.from(context).inflate(
				screenDialogResId, null, false);
		CustomizedDialog dialog = initDialog(contentView, true, true, context,
				-1);
		dialog.imgButton1 = (ImageButton) contentView
				.findViewById(R.id.on_off_icon1);
		dialog.imgButton2 = (ImageButton) contentView
				.findViewById(R.id.on_off_icon2);
		dialog.label1 = (TextView) contentView.findViewById(R.id.on_off_label1);
		dialog.label2 = (TextView) contentView.findViewById(R.id.on_off_label2);
		dialog.positiveButton = (Button) contentView
				.findViewById(R.id.positiveButton);
		View.OnClickListener listener = new View.OnClickListener() {
			public void onClick(View v) {
				v.setSelected(!v.isSelected());
			}
		};
		dialog.imgButton1.setOnClickListener(listener);
		dialog.imgButton2.setOnClickListener(listener);
		return dialog;
	}

	// FileSearcherDialog
	public static FileSearcherDialog initFileSearcherDialog(Context context,
			FileSystemAdapter adapter) {
		return new FileSearcherDialog(context, adapter);
	}

	// searchAdvancedDialog
	public static SearchAdvancedDialog initSearchAdvancedDialog(Context context) {
		return new SearchAdvancedDialog(context);
	}

	// searchAdvancedDialog
	public static FileSystemSettingsDialog initFileSystemSettingsDialog(
			Context context) {
		return new FileSystemSettingsDialog(context);
	}

	// MultiButtonDialog
	public static CustomizedDialog initMultiButtonDialog(Context context,
			String[] buttonNames) {
		View contentView = LayoutInflater.from(context).inflate(
				multiButtonResId, null, false);
		final CustomizedDialog dialog = initDialog(contentView, true, false,
				context, -1);
		dialog.message = ((TextView) contentView.findViewById(R.id.message));
		dialog.button1 = (Button) contentView.findViewById(R.id.button1);
		dialog.button2 = (Button) contentView.findViewById(R.id.button2);
		dialog.button3 = (Button) contentView.findViewById(R.id.button3);
		dialog.button4 = (Button) contentView.findViewById(R.id.button4);
		if (buttonNames != null && buttonNames.length > 0) {
			Button[] buttons = new Button[] { dialog.button1, dialog.button2,
					dialog.button3, dialog.button4 };
			for (int i = 0; i < buttons.length; i++) {
				if (buttonNames.length > i
						&& !TextUtils.isEmpty(buttonNames[i])) {
					buttons[i].setText(buttonNames[i]);
					buttons[i].setVisibility(View.VISIBLE);
				} else
					buttons[i].setVisibility(View.GONE);
			}
		}
		return dialog;
	}

	public static CustomizedDialog initHorizontalProgressDialog(Context context) {
		View contentView = LayoutInflater.from(context).inflate(progressResId,
				null, false);
		final CustomizedDialog dialog = initDialog(contentView, false, false,
				context, -1);

		dialog.pb1 = ((ProgressBar) contentView.findViewById(R.id.progressBar1));
		dialog.pb2 = ((ProgressBar) contentView.findViewById(R.id.progressBar2));
		dialog.imgButton1 = (ImageButton) contentView
				.findViewById(R.id.imageButton1);
		dialog.label1 = (TextView) contentView.findViewById(R.id.textView1);
		dialog.label2 = (TextView) contentView.findViewById(R.id.textView2);
		return dialog;
	}

	public static CustomizedDialog initDialog(View contentView,
			boolean cancelable, boolean cancelOnTouchOutside, Context context,
			float width) {
		CustomizedDialog dialog = new CustomizedDialog(context,
				R.style.dialogStyle);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCancelable(cancelable);
		dialog.setCanceledOnTouchOutside(cancelOnTouchOutside);
		// dialog.setContentView(contentView);
		dialog.getWindow().setContentView(contentView,
				new LayoutParams((int) width, -1));
		return dialog;
	}

	/**
	 * 
	 * @param title
	 * @param message
	 * @param cancelOnTouchOutside
	 * @param context
	 * @return
	 */

	public static ProgressDialog createProgressDialogNshow(String title,
			String message, boolean cancelOnTouchOutside, Context context) {
		ProgressDialog pd = new ProgressDialog(context,
				ProgressDialog.STYLE_SPINNER);
		pd.setTitle(title);
		pd.setMessage(message);
		pd.setCanceledOnTouchOutside(cancelOnTouchOutside);
		pd.show();
		return pd;
	}

	/**
	 * 
	 * @author Administrator
	 * @name Builder
	 */
	public static class Builder {
		Context context;
		String title, message, positiveButton, negativeButton, checkedText;
		float fontSize;
		DialogInterface.OnClickListener positiveButtonListener,
				negativeButtonListener;

		public Builder(Context context) {
			this.context = context;
		}

		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}

		public Builder setMessage(String message) {
			this.message = message;
			return this;
		}

		public Builder setCheckedText(String text) {
			this.checkedText = text;
			return this;
		}

		public Builder setContentFontSize(float size) {
			this.fontSize = size;
			return this;
		}

		public Builder setPositiveButton(String name,
				DialogInterface.OnClickListener listener) {
			this.positiveButton = name;
			this.positiveButtonListener = listener;
			return this;
		}

		public Builder setNegativeButton(String name,
				DialogInterface.OnClickListener listener) {
			this.negativeButton = name;
			this.negativeButtonListener = listener;
			return this;
		}

		public CustomizedDialog create() {
			final CustomizedDialog dialog = initDialog(title, message,
					checkedText, fontSize, context);
			if (positiveButton != null) {
				dialog.positiveButton.setVisibility(View.VISIBLE);
				dialog.positiveButton.setText(positiveButton);
				dialog.positiveButton
						.setOnClickListener(new View.OnClickListener() {
							public void onClick(View v) {
								if (positiveButtonListener != null)
									positiveButtonListener.onClick(dialog,
											DialogInterface.BUTTON_POSITIVE);
								dialog.dismiss();
							}
						});
			} else {
				dialog.positiveButton.setVisibility(View.GONE);
			}
			if (negativeButton != null) {
				dialog.negativeButton.setVisibility(View.VISIBLE);
				dialog.negativeButton.setText(negativeButton);
				dialog.negativeButton
						.setOnClickListener(new View.OnClickListener() {
							public void onClick(View v) {
								if (negativeButtonListener != null)
									negativeButtonListener.onClick(dialog,
											DialogInterface.BUTTON_NEGATIVE);
								dialog.dismiss();
							}
						});
			} else {
				dialog.negativeButton.setVisibility(View.GONE);
			}
			LayoutParams lp = dialog.getWindow().getAttributes();
			System.out.println("height=" + lp.height + ",width=" + lp.width
					+ "   @CustomizedDialog");
			return dialog;
		}
	}

	/***
	 * innerclass
	 * 
	 * @author Administrator
	 * 
	 */

	public static class FileSearcherDialog extends Dialog implements
			View.OnClickListener {

		public TextView title, label, dropDown;

		public EditText searchKeyField;

		public Button searchKeyFieldDisposer, positiveButton, negativeButton;

		public ImageButton sort, screen, copy, move, delete;

		public GridView gv;

		public View dmc;

		public SearchAdvancedDialog fileSearchAdvancedDialog;

		public FileSystemAdapter fileSystemAdapter;

		public FileSearcherAdapter fileSearcherAdapter;

		public ProgressBar pb;

		public CustomizedDialog fileOrderDialog;

		public OptionListAdapter optionListAdapter;

		public View header;

		public FileSearcherDialog(Context context,
				FileSystemAdapter fileSystemAdapter) {
			super(context, R.style.dialogStyle);
			this.fileSystemAdapter = fileSystemAdapter;
			setCancelable(true);
			setCanceledOnTouchOutside(false);
			requestWindowFeature(Window.FEATURE_NO_TITLE);

			View contentView = LayoutInflater.from(getContext()).inflate(
					searcherResId, null, false);
			getWindow().setContentView(
					contentView,
					new LayoutParams((int) getContext().getResources()
							.getDimension(R.dimen.dp_350), -1));
			setDialogsWindowSoftInputModeToHideAlways(this);
			init();
			initGv();
			bindListeners();

		}

		private void initFileOrderDialogNshow(int type) {
			if (fileOrderDialog == null) {
				fileOrderDialog = CustomizedDialog
						.initOptionDialog(getContext());
				fileOrderDialog.lv
						.setAdapter(optionListAdapter = new OptionListAdapter(
								getContext()));
				fileOrderDialog.lv
						.setOnItemClickListener(new OnItemClickListener() {
							public void onItemClick(AdapterView<?> parent,
									View self, int position, long id) {
								int type = optionListAdapter.type;
								if (type == FileManager.TYPE_SORT) {
									fileSearcherAdapter.sort(position);
									fileOrderDialog.dismiss();
								} else if (type == FileManager.TYPE_SCREEN) {
									fileSearcherAdapter.screen(position);
									fileOrderDialog.dismiss();
								}
							}
						});
			}

			optionListAdapter
					.setType(null, fileOrderDialog.title, type, null,
							fileSearcherAdapter.comparator,
							fileSearcherAdapter.iFilter);
			fileOrderDialog.show();
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
		}

		public void setScope(File file) {
			if (!fileSearcherAdapter.isStop())
				return;
			title.setText(FileUtils.getLinuxFileName(file));
			if (fileSearcherAdapter != null)
				fileSearcherAdapter.setScope(file);
		}

		public void setScope(List<File> files) {
			if (!fileSearcherAdapter.isStop())
				return;
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < files.size(); i++) {
				sb.append(files.get(i).getName() + " |");
			}
			sb.deleteCharAt(sb.length() - 1);
			title.setText(sb.toString());
			if (fileSearcherAdapter != null)
				fileSearcherAdapter.setScope(files);
		}

		private void init() {
			searchKeyField = (EditText) findViewById(R.id.editText1);
			searchKeyFieldDisposer = (Button) findViewById(R.id.button1);
			dropDown = (TextView) findViewById(R.id.dropdown);
			gv = (GridView) findViewById(R.id.gridView1);
			sort = (ImageButton) findViewById(R.id.sort);
			screen = (ImageButton) findViewById(R.id.screen);
			positiveButton = (Button) findViewById(R.id.positiveButton);
			negativeButton = (Button) findViewById(R.id.negativeButton);
			title = (TextView) findViewById(R.id.title);
			label = (TextView) findViewById(R.id.label);
			dmc = findViewById(R.id.linearLayout1);
			copy = (ImageButton) findViewById(R.id.copy);
			move = (ImageButton) findViewById(R.id.move);
			delete = (ImageButton) findViewById(R.id.delete);
			pb = (ProgressBar) findViewById(R.id.progressBar1);
			header = findViewById(R.id.header);

		}

		private void initGv() {
			gv.setAdapter(fileSearcherAdapter = new FileSearcherAdapter(
					getContext(), fileSystemAdapter, dmc, sort, screen,
					positiveButton, dropDown, pb));
		}

		private void bindListeners() {
			CommonUtils.bindEditTextNtextDisposer(searchKeyField,
					searchKeyFieldDisposer);
			header.setOnClickListener(this);
			negativeButton.setOnClickListener(this);
			dropDown.setOnClickListener(this);
			sort.setOnClickListener(this);
			screen.setOnClickListener(this);
			positiveButton.setOnClickListener(this);
			copy.setOnClickListener(this);
			move.setOnClickListener(this);

		}

		public void onClick(View v) {
			if (v == negativeButton || v == header) {
				dismiss();
			} else if (v == dropDown) {
				initFileSearchAdvancedDialogNshow();
			} else if (v == sort) {
				initFileOrderDialogNshow(FileManager.TYPE_SORT);
			} else if (v == screen) {
				initFileOrderDialogNshow(FileManager.TYPE_SCREEN);
			} else if (v == move) {
				fileSearcherAdapter.addSelectedToMoveList();
			} else if (v == copy) {
				fileSearcherAdapter.addSelectedToCopyList();
			} else if (v == positiveButton) {
				if (/*
					 * positiveButton.getText().toString()
					 * .equals(getContext().getString(R.string.startSearch))
					 */fileSearcherAdapter.isStop()) {
					String key = searchKeyField.getText().toString();
					if (TextUtils.isEmpty(key)) {
						CommonUtils.toast("请输入要查找的关键字。");
					} else {
						fileSearcherAdapter.startSearch(key,
								getSearchFileFilter());
					}
				} else {
					fileSearcherAdapter.stopSearch();
				}
			}
		}

		private void initFileSearchAdvancedDialogNshow() {
			if (fileSearchAdvancedDialog == null) {
				fileSearchAdvancedDialog = CustomizedDialog
						.initSearchAdvancedDialog(getContext());
				fileSearchAdvancedDialog
						.setOnDismissListener(new OnDismissListener() {
							public void onDismiss(DialogInterface dialog) {
								String re = null;
								if ((re = fileSearchAdvancedDialog
										.resolveTemplateToRE()) != null) {
									searchKeyField.setText(re);
								}
							}
						});
			}
			fileSearchAdvancedDialog.show();
		}

		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				backOperation();
				return true;
			} else
				return super.onKeyDown(keyCode, event);
		}

		private void backOperation() {
			if (fileSearcherAdapter != null
					&& fileSearcherAdapter.multiselectorMode)
				fileSearcherAdapter.toggleMode();
			else {
				dismiss();
			}
		}

		private ConditionalSearchFileFilter getSearchFileFilter() {
			if (fileSearchAdvancedDialog == null)
				return null;
			return fileSearchAdvancedDialog.getConditionalFilter();
		}

		@Override
		public void dismiss() {
			if (fileSearchAdvancedDialog != null)
				fileSearchAdvancedDialog.dismiss();
			if (fileOrderDialog != null)
				fileOrderDialog.dismiss();
			super.dismiss();
		}

		public void stopSearch() {
			fileSearcherAdapter.stopSearch();
		}

		public int onItemClicked(View self, int position) {
			int type = fileSearcherAdapter.onItemClicked(self, position);
			if (type == -2)
				dismiss();
			return type;
		}

	}

	public static class SearchAdvancedDialog extends Dialog implements
			View.OnClickListener {

		public CheckedTextView checkedText1, checkedText2, checkedText3,

		checkedText4, checkedText5, checkedText6, checkedText7, checkedText8,
				checkedText9;

		public Button button1;

		public ViewGroup vg1, vg2;

		public TextView tv1, tv2, tv3, tv4, editTemplate;

		public EditText et1, et2, et3, et4;

		public StrAryPopWindow popWindow;

		public String[] sizeAry, dateAry;

		public TemplateViewHolder template;

		public static final int[] TimeUnit = new int[] { Calendar.HOUR,
				Calendar.DATE, Calendar.MONTH, Calendar.YEAR };

		private void toggle1(boolean b) {
			tv1.setEnabled(b);
			tv2.setEnabled(b);
			et1.setEnabled(b);
			et2.setEnabled(b);
		}

		private void toggle2(boolean b) {
			tv3.setEnabled(b);
			tv4.setEnabled(b);
			et3.setEnabled(b);
			et4.setEnabled(b);
		}

		private void toggle3(boolean b) {
			editTemplate.setEnabled(b);
			if (!b)
				closeTemplate();
		}

		private void toggleTemplate() {
			if (editTemplate.isActivated())
				closeTemplate();
			else
				editTemplate();

		}

		private void editTemplate() {
			template.show();
			editTemplate.setText(getContext().getString(
					R.string.closeRETemplate));
			editTemplate.setActivated(true);
		}

		private void closeTemplate() {
			template.gone();
			editTemplate.setText(getContext()
					.getString(R.string.editRETemplate));
			editTemplate.setActivated(false);
		}

		public String resolveTemplateToRE() {
			if (checkedText2.isChecked() && template.isVisible())
				return template.resolveTemplateToRE();
			return null;
		}

		public ConditionalSearchFileFilter getConditionalFilter() {
			ConditionalSearchFileFilter filter = new ConditionalSearchFileFilter();
			filter.file = checkedText5.isChecked();
			filter.directory = checkedText6.isChecked();
			filter.systemFile = checkedText7.isChecked();
			filter.sizeRestriction = checkedText8.isChecked();
			filter.timeRestriction = checkedText9.isChecked();
			filter.caseSensitive = checkedText1.isChecked();
			filter.regularExpression = checkedText2.isChecked();
			filter.writeable = checkedText3.isChecked();
			filter.readable = checkedText4.isChecked();
			if (filter.sizeRestriction) {
				String s1 = StringUtil.trimSpaces(et1.getText().toString());
				String s2 = StringUtil.trimSpaces(et2.getText().toString());
				String str1 = tv1.getText().toString();
				String str2 = tv2.getText().toString();
				long gt = 0, lt = 0;
				if (StringUtil.isNumber(s1)) {
					gt = Integer.parseInt(s1);
					for (int i = 0; i < 4 && !sizeAry[i].equals(str1); i++) {
						gt *= 1024;
					}
				} else {
					if (!TextUtils.isEmpty(s1)) {
						CommonUtils.toast("大于大小值非整数，被忽略。");
					}
				}

				if (StringUtil.isNumber(s2)) {
					lt = Integer.parseInt(s2);
					for (int i = 0; i < 4 && !sizeAry[i].equals(str2); i++) {
						lt *= 1024;
					}
				} else {
					if (!TextUtils.isEmpty(s2)) {
						CommonUtils.toast("小于大小值非整数，被忽略。");
					}
				}

				filter.gtSize = gt;
				filter.ltSize = lt;
			}
			if (filter.timeRestriction) {
				String s3 = StringUtil.trimSpaces(et3.getText().toString());
				String s4 = StringUtil.trimSpaces(et4.getText().toString());
				String str3 = tv3.getText().toString();
				String str4 = tv4.getText().toString();
				long gt = 0, lt = 0;
				if (StringUtil.isNumber(s3)) {
					int n = Integer.parseInt(s3);
					Calendar calendar = Calendar.getInstance();
					calendar.add(
							TimeUnit[StringUtil.indexOfAry(str3, dateAry)], -n);
					gt = calendar.getTimeInMillis();
				} else {
					if (!TextUtils.isEmpty(s3)) {
						CommonUtils.toast("大于时间值非整数，被忽略。");
					}
				}
				if (StringUtil.isNumber(s4)) {
					int n = Integer.parseInt(s4);
					Calendar calendar = Calendar.getInstance();
					calendar.add(
							TimeUnit[StringUtil.indexOfAry(str4, dateAry)], -n);
					lt = calendar.getTimeInMillis();
				} else {
					if (!TextUtils.isEmpty(s4)) {
						CommonUtils.toast("小于时间值非整数，被忽略。");
					}
				}

				filter.gtTime = gt;
				filter.ltTime = lt;

			}

			return filter;
		}

		public SearchAdvancedDialog(Context context) {
			super(context, R.style.dialogStyle);
			setCancelable(true);
			setCanceledOnTouchOutside(false);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			View contentView = LayoutInflater.from(getContext()).inflate(
					searchAdvancedResId, null, false);
			setContentView(contentView, new LayoutParams((int) getContext()
					.getResources().getDimension(R.dimen.dp_350), -1));
			setDialogsWindowSoftInputModeToHideAlways(this);
			init();
			bindListeners();
		}

		private void initNshowPopWindow(TextView trig) {
			if (popWindow == null) {
				popWindow = new StrAryPopWindow(getContext());
			}

			String[] sary = null;
			if (trig == tv1 || trig == tv2)
				sary = sizeAry;
			else if (trig == tv3 || trig == tv4)
				sary = dateAry;
			popWindow.update(trig, sary, true, trig.getWidth(), null);
			popWindow.showAsDropDown();
		}

		private void bindListeners() {
			checkedText1.setOnClickListener(this);
			checkedText2.setOnClickListener(this);
			checkedText3.setOnClickListener(this);
			checkedText4.setOnClickListener(this);
			checkedText5.setOnClickListener(this);
			checkedText6.setOnClickListener(this);
			checkedText7.setOnClickListener(this);
			checkedText8.setOnClickListener(this);
			checkedText9.setOnClickListener(this);
			tv1.setOnClickListener(this);
			tv2.setOnClickListener(this);
			tv3.setOnClickListener(this);
			tv4.setOnClickListener(this);
			editTemplate.setOnClickListener(this);
			button1.setOnClickListener(this);
		}

		private void init() {
			checkedText1 = (CheckedTextView) findViewById(R.id.checkedTextView1);
			checkedText2 = (CheckedTextView) findViewById(R.id.checkedTextView2);
			checkedText3 = (CheckedTextView) findViewById(R.id.checkedTextView3);
			checkedText4 = (CheckedTextView) findViewById(R.id.checkedTextView4);
			checkedText5 = (CheckedTextView) findViewById(R.id.checkedTextView5);
			checkedText6 = (CheckedTextView) findViewById(R.id.checkedTextView6);
			checkedText7 = (CheckedTextView) findViewById(R.id.checkedTextView7);
			checkedText8 = (CheckedTextView) findViewById(R.id.checkedTextView8);
			checkedText9 = (CheckedTextView) findViewById(R.id.checkedTextView9);
			vg1 = (ViewGroup) findViewById(R.id.linearLayout1);
			vg2 = (ViewGroup) findViewById(R.id.linearLayout2);
			tv1 = (TextView) findViewById(R.id.textView1);
			tv2 = (TextView) findViewById(R.id.textView2);
			tv3 = (TextView) findViewById(R.id.textView3);
			tv4 = (TextView) findViewById(R.id.textView4);
			editTemplate = (TextView) findViewById(R.id.textView5);
			et1 = (EditText) findViewById(R.id.editText1);
			et2 = (EditText) findViewById(R.id.editText2);
			et3 = (EditText) findViewById(R.id.editText3);
			et4 = (EditText) findViewById(R.id.editText4);

			button1 = (Button) findViewById(R.id.button1);

			template = new TemplateViewHolder(findViewById(R.id.retemplate),
					getContext());

			sizeAry = getContext().getResources().getStringArray(
					R.array.sizeAry);
			dateAry = getContext().getResources().getStringArray(
					R.array.dateAry);

			tv1.setText(sizeAry[1]);
			tv2.setText(sizeAry[1]);
			tv3.setText(dateAry[1]);
			tv4.setText(dateAry[1]);

			toggle1(false);
			toggle2(false);
			toggle3(false);
		}

		@Override
		public void dismiss() {
			if (popWindow != null)
				popWindow.dismiss();
			super.dismiss();
		}

		@Override
		public void onClick(View v) {
			if (v == button1) {
				dismiss();
			} else if (v == editTemplate) {
				toggleTemplate();
			} else if (v instanceof CheckedTextView) {
				CheckedTextView ctv = (CheckedTextView) v;
				boolean b = ctv.isChecked();
				ctv.setChecked(!b);
				if (v == checkedText8)
					toggle1(!b);
				else if (v == checkedText9)
					toggle2(!b);
				else if (v == checkedText2)
					toggle3(!b);
			} else if (v instanceof TextView) {
				initNshowPopWindow((TextView) v);
			}
		}

		// TODO
		static class TemplateViewHolder implements View.OnClickListener {

			public static final String[] extensionList = new String[] { "txt",
					"xml", "xhtml,html", "pdf", "doc,docx", "xls,xlsx",
					"ppt,pptx", "jpg", "jpeg", "gif", "png", "bmp", "wbmp",
					"xzip", "zip", "rar", "gzip", "bzip2", "tar", "jar",
					CommonUtils.context.getString(R.string.videoSuffix),
					CommonUtils.context.getString(R.string.audioSuffix), "apk" };

			public Context context;

			public View container;

			public CheckedTextView nosuffix, docs, imgs, compressed;

			public EditText keyword, extension;

			public CheckedTextView[] extensionCTlist = new CheckedTextView[23];

			public TemplateViewHolder(View container, Context context) {
				this.container = container;
				this.context = context;
				init();
				bindListeners();
			}

			private void init() {
				keyword = (EditText) container.findViewById(R.id.et1);
				extension = (EditText) container.findViewById(R.id.et2);
				nosuffix = (CheckedTextView) container
						.findViewById(R.id.nosuffix);
				docs = (CheckedTextView) container.findViewById(R.id.ct0);
				imgs = (CheckedTextView) container.findViewById(R.id.ct1);
				compressed = (CheckedTextView) container.findViewById(R.id.ct5);
				String packageName = context.getPackageName();
				for (int i = extensionCTlist.length - 1; i >= 0; i--) {
					extensionCTlist[i] = (CheckedTextView) container
							.findViewById(context.getResources().getIdentifier(
									"ct"
											+ ((i + 1) > 9 ? (i + 1)
													: ("0" + (i + 1))), "id",
									packageName));
				}
			}

			private void bindListeners() {
				nosuffix.setOnClickListener(this);
				docs.setOnClickListener(this);
				imgs.setOnClickListener(this);
				compressed.setOnClickListener(this);
				for (int i = extensionCTlist.length - 1; i >= 0; i--) {
					extensionCTlist[i].setOnClickListener(this);
				}
			}

			private void toggleExtensions(boolean b) {
				extension.setEnabled(b);
				docs.setEnabled(b);
				imgs.setEnabled(b);
				compressed.setEnabled(b);
				for (int i = extensionCTlist.length - 1; i >= 0; i--) {
					extensionCTlist[i].setEnabled(b);
				}
			}

			private void toggleCTlist(boolean b, int start, int end) {
				for (int i = start; i < end; i++) {
					extensionCTlist[i].setChecked(b);
				}
			}

			private void checkCTstate() {
				boolean b1 = true, b2 = true, b3 = true;
				for (int i = 0; i < 7; i++) {
					b1 &= extensionCTlist[i].isChecked();
					if (!b1)
						break;
				}
				for (int i = 7; i < 13; i++) {
					if (!extensionCTlist[i].isChecked()) {
						b2 = false;
						break;
					}
				}
				for (int i = 13; i < 20; i++) {
					if (!extensionCTlist[i].isChecked()) {
						b3 = false;
						break;
					}
				}

				docs.setChecked(b1);
				imgs.setChecked(b2);
				compressed.setChecked(b3);
			}

			public void gone() {
				container.setVisibility(View.GONE);
			}

			public void show() {
				container.setVisibility(View.VISIBLE);
			}

			public boolean isVisible() {
				return container.getVisibility() == View.VISIBLE;
			}

			public String resolveExtensionsToStr() {
				StringBuilder sb = new StringBuilder();
				int len = extensionCTlist.length;
				for (int i = 0; i < len; i++) {
					if (extensionCTlist[i].isChecked()) {
						sb.append(extensionList[i]);
						sb.append(",");
					}
				}
				if (sb.length() > 0)
					sb.deleteCharAt(sb.length() - 1);
				return sb.toString();
			}

			public String resolveTemplateToRE() {
				String re = null;
				String key = keyword.getText().toString();
				if (nosuffix.isChecked()) {
					if (TextUtils.isEmpty(key))
						re = "[^/\\\\\\.]*";
					else
						re = "[^/\\\\]*" + key + "[^/\\\\\\.]*";
				} else {
					String extend = extension.getText().toString()
							.replace(',', '|');
					if (TextUtils.isEmpty(key))
						re = "[^/\\\\]*\\.(?:" + extend + ")";
					else
						re = "[^/\\\\]*" + key + "[^/\\\\]*\\.(?:" + extend
								+ ")";
				}
				return re;
			}

			public void onClick(View v) {
				if (v instanceof CheckedTextView) {
					CheckedTextView ct = (CheckedTextView) v;
					boolean b = !ct.isChecked();
					ct.setChecked(b);
					if (v == nosuffix) {
						toggleExtensions(!b);
					} else {
						if (v == docs) {
							toggleCTlist(b, 0, 7);
						} else if (v == imgs) {
							toggleCTlist(b, 7, 13);
						} else if (v == compressed) {
							toggleCTlist(b, 13, 20);
						} else {
							checkCTstate();
						}
						extension.setText(resolveExtensionsToStr());
					}
				}
			}
		}

	}

	public static class FileSystemSettingsDialog extends Dialog implements
			View.OnClickListener {

		public static final int factor = 50;

		public TextView plus1, tv1, minus1, plus2, tv2, minus2;

		public SeekBar seekbar;

		public Button apply;

		public Runnable callback;

		private int colCount, maxResultCount;

		public FileSystemSettingsDialog(Context context) {
			super(context, R.style.dialogStyle);
			setCancelable(true);
			setCanceledOnTouchOutside(true);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			View contentView = LayoutInflater.from(getContext()).inflate(
					fileSystemSettingsDialogResId, null, false);
			setContentView(
					contentView,
					new LayoutParams(getContext().getResources()
							.getDimensionPixelSize(R.dimen.dp_320),
							getContext().getResources().getDimensionPixelSize(
									R.dimen.dp_320)));
			setDialogsWindowSoftInputModeToHideAlways(this);
			init();
			bindListeners();
		}

		private void init() {
			minus1 = (TextView) findViewById(R.id.textView1);
			tv1 = (TextView) findViewById(R.id.textView2);
			plus1 = (TextView) findViewById(R.id.textView3);
			minus2 = (TextView) findViewById(R.id.textView4);
			tv2 = (TextView) findViewById(R.id.textView5);
			plus2 = (TextView) findViewById(R.id.textView6);
			apply = (Button) findViewById(R.id.button1);
			seekbar = (SeekBar) findViewById(R.id.seekBar1);
			seekbar.setMax(19);

		}

		private void bindListeners() {
			minus1.setOnClickListener(this);
			plus1.setOnClickListener(this);
			minus2.setOnClickListener(this);
			plus2.setOnClickListener(this);
			apply.setOnClickListener(this);
			seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
				public void onStopTrackingTouch(SeekBar seekBar) {
				}

				public void onStartTrackingTouch(SeekBar seekBar) {
				}

				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					if (fromUser)
						setMaxResultCount(progress + 1);
				}
			});

		}

		public void setColCount(int col) {
			this.colCount = col > 8 ? 8 : col < 1 ? 1 : col;
			update1();
		}

		private void update1() {
			tv1.setText(String.valueOf(getColCount()));
			minus1.setEnabled(colCount > 1);
			plus1.setEnabled(colCount < 8);
		}

		public int getColCount() {
			return colCount;
		}

		public int getMaxResultCount() {
			return maxResultCount * factor;
		}

		public void setMaxResultCount(int max) {
			this.maxResultCount = max < 1 ? 1 : max > 20 ? 20 : max;
			update2();
		}

		private void update2() {
			tv2.setText(String.valueOf(getMaxResultCount()));
			minus2.setEnabled(maxResultCount > 1);
			plus2.setEnabled(maxResultCount < 20);
			seekbar.setProgress(maxResultCount - 1);
		}

		public void setApplyCallBack(Runnable runnable) {
			this.callback = runnable;
		}

		@Override
		public void onClick(View v) {
			if (v == minus1) {
				setColCount(colCount - 1);
			} else if (v == plus1) {
				setColCount(colCount + 1);
			} else if (v == plus2) {
				setMaxResultCount(maxResultCount + 1);
			} else if (v == minus2) {
				setMaxResultCount(maxResultCount - 1);
			} else if (v == apply) {
				callback.run();
				dismiss();
			}

		}

		@Override
		public void show() {
			super.show();
			setColCount(SharePrefUtil.getFileSystemGridColumnNum());
			setMaxResultCount(SharePrefUtil.getMaxFileSystemSearchResultCount()
					/ factor);
		}

	}

}
